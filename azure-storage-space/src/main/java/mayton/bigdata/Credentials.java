package mayton.bigdata;

final class Credentials {

    public final String label;
    public final String dfsEndpoint;
    public final String primaryKey;
    public final String connectionString;

    public Credentials(String label, String dfsEndpoint, String primaryKey, String connectionString) {
        this.label = label;
        this.dfsEndpoint = dfsEndpoint;
        this.primaryKey = primaryKey;
        this.connectionString = connectionString;
    }
}
