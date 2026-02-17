package mayton.bigdata;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

import static mayton.bigdata.Utils.println;

import java.time.Duration;

public class AssReport {

    static String formatInteval(long sec) {
        Duration d = Duration.ofSeconds(sec);
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    static String kb(long size, int k) {
        String[] mult = {"bytes","K","M","G","T"};
        if (size < 1024 || k == mult.length) {
            return "" + size + " " + mult[k];
        } else {
            return kb(size / 1024, k + 1);
        }
    }

    public static Pair<Long, Long> calculateContainerSize(BlobServiceClient serviceClient, String containerName,
                                                          Function<String,Boolean> blobNameFilter) {

        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(containerName);

        long totalSize = 0;
        int cnt = 0;

        for (BlobItem blobItem : containerClient.listBlobs()) {
            String blobName = blobItem.getName();
            if (blobNameFilter.apply(blobName)) {
                BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
                BlobProperties properties = blobClient.getProperties();
                long size = properties.getBlobSize();
                totalSize += size;
                cnt++;
            }
        }

        System.out.printf("Count : %d\n", cnt);
        System.out.printf("AVG size : %d bytes\n", totalSize / cnt);

        return Pair.of(totalSize, (long) cnt);
    }

    public static void main(String[] args) throws Exception {

        boolean continueMode = true;

        DbComponent db = new DbComponent();
        db.initDb();

        List<Credentials> creds = Arrays.asList(
                new Credentials(
                        "dev",
                        "https://***.dfs.core.windows.net",
                        "***",
                        "")
        );
        PrintWriter pw = new PrintWriter("report.md");
        pw.println("# Azure Storage space report:");

        pw.println("|Label|Container|Size|Files count|AVG size|");
        pw.println("|-----|---------|----|-----------|--------|");

        List<String> containers = Arrays.asList("raw");

        long begin = System.nanoTime();

        for(Credentials cred : creds) {
            println("Processing label : " + cred.label);
            BlobServiceClient serviceClient =
                    new BlobServiceClientBuilder()
                            .connectionString(cred.connectionString)
                            .buildClient();

            for(String container : containers) {
                println("..Container : " + container);
                Pair<Long, Long> res = calculateContainerSize(
                        serviceClient,
                        container, s -> s.endsWith(".gz") || s.endsWith(".parquet"));

                long size = res.getLeft();
                long cnt = res.getRight();
                String sizeStr = kb(size, 0);

                pw.println("|" + cred.label + "|" + container + "|" + sizeStr + "|" + cnt + "|" + size / cnt);
            }
        }

        long end = System.nanoTime();

        long sec = (end - begin) / 1000 / 1000 / 1000;

        pw.printf("\nElapsed time: %d sec (%s)\n", sec, formatInteval(sec));

        pw.close();


    }
}
