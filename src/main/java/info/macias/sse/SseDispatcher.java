package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class SseDispatcher {
    private final AsyncContext asyncContext;

    public SseDispatcher(HttpServletRequest request) {
        asyncContext = request.startAsync();
        asyncContext.setTimeout(0);
        asyncContext.addListener(new AsyncListenerImpl());
    }

    public SseDispatcher ok() {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.setStatus(200);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Connection","keep-alive");
        return this;
    }

    public SseDispatcher open() throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print("event: open\n\n");
        response.getOutputStream().flush();

        return this;
    }

    public SseDispatcher send(String event, String data) throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
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

    public SseDispatcher send(MessageEvent me) throws IOException {
		HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print(me.toString());
		response.getOutputStream().flush();
        return this;
    }

    private boolean completed = false;
    public void close() {
        if(!completed) asyncContext.complete();
    }

    private class AsyncListenerImpl implements AsyncListener {
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            completed = true;
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }
}
