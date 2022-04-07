package at.fhtw.bif3.swe.mtcg.if19b017.server.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Request
{
    private RequestHttp RequestHttp;
    private String pathname;
    private String params;
    private String contentType;
    private Integer contentLength;
    private String token;
    private String body = "";
}
