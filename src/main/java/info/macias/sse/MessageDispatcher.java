package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
public class MessageDispatcher implements AsyncListener {
    private final AsyncContext asyncContext;

    public MessageDispatcher(HttpServletRequest request) {
        asyncContext = request.startAsync();
        asyncContext.setTimeout(0);
        asyncContext.addListener(this);
    }

    public MessageDispatcher ok() {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.setStatus(200);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Connection","keep-alive");
        return this;
    }

    public MessageDispatcher open() throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print("event: open\n\n");
        response.getOutputStream().flush();

        return this;
    }

    public MessageDispatcher send(String event, String data) throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        ServletOutputStream out = response.getOutputStream();
//        while(!out.isReady()) Thread.yield();
        response.getOutputStream().print(
                new MessageEvent.Builder()
                    .setData(data)
                    .setEvent(event)
                    .build()
                    .toString()
        );
        response.getOutputStream().flush();
        return this;
    }

    public MessageDispatcher send(MessageEvent me) throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print(me.toString());
        return this;
    }

    private boolean completed = false;
    public void close() {
        if(!completed) asyncContext.complete();
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        System.out.println("***** On complete ******: " + event.getThrowable());
        completed = true;
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        System.out.println("***** On timeout ******: " + event.getThrowable());
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        System.out.println("***** On error ******: " + event.getThrowable());

    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        System.out.println("***** On startasync ******: " + event.getThrowable());

    }
}