package mayton.bigdata.formatters;

public class ExportException extends Exception {

    enum ExportErrorCode {
        SCHEMA_PHASE,
        DATA_PHASE,
        UNKNOWN
    }

    ExportErrorCode code = ExportErrorCode.UNKNOWN;

    public ExportException(String comment) {
        super(comment);
        this.code = ExportErrorCode.UNKNOWN;
    }

    public ExportException(String comment, ExportErrorCode code) {
        super(comment);
        this.code = code;
    }

}
