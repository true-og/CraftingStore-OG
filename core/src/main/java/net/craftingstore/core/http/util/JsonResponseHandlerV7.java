package net.craftingstore.core.http.util;

import com.google.gson.Gson;
import java.io.IOException;
import net.craftingstore.core.models.api.RootV7;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class JsonResponseHandlerV7<T> implements ResponseHandler<T> {

    private Gson gson;
    private Class<T> aClass;

    public JsonResponseHandlerV7(Gson gson, Class<T> aClass) {
        this.gson = gson;
        this.aClass = aClass;
    }

    public T handleResponse(HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();

        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        String value = EntityUtils.toString(entity);

        if (aClass == RootV7.class) {
            return gson.fromJson(value, aClass);
        }
        RootV7<T> result = gson.fromJson(value, new GenericOf<>(RootV7.class, aClass));
        return result.getData();
    }
}
