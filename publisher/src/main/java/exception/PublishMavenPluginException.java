package exception;

public class PublishMavenPluginException extends Throwable {
    public final String code;
    public final String description;
    public final Throwable cause;

    public PublishMavenPluginException(String code, String description, Throwable cause){
        super(code + ":" + description, cause);
        this.code = code;
        this.description = description;
        this.cause = cause;
    }
}
