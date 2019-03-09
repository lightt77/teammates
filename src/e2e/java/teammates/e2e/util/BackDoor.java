package teammates.e2e.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;

/**
 * Used to create API calls to the back-end without going through the UI.
 *
 * <p>Note that this will replace {@link teammates.test.driver.BackDoor} once the front-end migration is complete.
 */
public final class BackDoor {

    private BackDoor() {
        // Utility class
    }

    /**
     * Executes GET request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public static ResponseBodyAndCode executeGetRequest(String relativeUrl, Map<String, String[]> params) {
        return executeRequest(HttpGet.METHOD_NAME, relativeUrl, params, null);
    }

    /**
     * Executes POST request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public static ResponseBodyAndCode executePostRequest(String relativeUrl, Map<String, String[]> params, String body) {
        return executeRequest(HttpPost.METHOD_NAME, relativeUrl, params, body);
    }

    /**
     * Executes PUT request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public static ResponseBodyAndCode executePutRequest(String relativeUrl, Map<String, String[]> params, String body) {
        return executeRequest(HttpPut.METHOD_NAME, relativeUrl, params, body);
    }

    /**
     * Executes DELETE request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public static ResponseBodyAndCode executeDeleteRequest(String relativeUrl, Map<String, String[]> params) {
        return executeRequest(HttpDelete.METHOD_NAME, relativeUrl, params, null);
    }

    /**
     * Executes HTTP request with the given {@code method} and {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    private static ResponseBodyAndCode executeRequest(
            String method, String relativeUrl, Map<String, String[]> params, String body) {
        String url = TestProperties.TEAMMATES_URL + Const.ResourceURIs.URI_PREFIX + relativeUrl;

        HttpRequestBase request;
        switch (method) {
        case HttpGet.METHOD_NAME:
            request = createGetRequest(url, params);
            break;
        case HttpPost.METHOD_NAME:
            request = createPostRequest(url, params, body);
            break;
        case HttpPut.METHOD_NAME:
            request = createPutRequest(url, params, body);
            break;
        case HttpDelete.METHOD_NAME:
            request = createDeleteRequest(url, params);
            break;
        default:
            throw new RuntimeException("Unaccepted HTTP method: " + method);
        }

        addAuthKeys(request);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            String responseBody = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                    responseBody = br.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
            return new ResponseBodyAndCode(responseBody, response.getStatusLine().getStatusCode());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes GET request with the given {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    private static HttpGet createGetRequest(String url, Map<String, String[]> params) {
        return new HttpGet(createBasicUri(url, params));
    }

    private static HttpPost createPostRequest(String url, Map<String, String[]> params, String body) {
        HttpPost post = new HttpPost(createBasicUri(url, params));

        if (body != null) {
            StringEntity entity = new StringEntity(body, Charset.forName("UTF-8"));
            post.setEntity(entity);
        }

        return post;
    }

    private static HttpPut createPutRequest(String url, Map<String, String[]> params, String body) {
        HttpPut put = new HttpPut(createBasicUri(url, params));

        if (body != null) {
            StringEntity entity = new StringEntity(body, Charset.forName("UTF-8"));
            put.setEntity(entity);
        }

        return put;
    }

    private static HttpDelete createDeleteRequest(String url, Map<String, String[]> params) {
        return new HttpDelete(createBasicUri(url, params));
    }

    private static URI createBasicUri(String url, Map<String, String[]> params) {
        List<NameValuePair> postParameters = new ArrayList<>();
        if (params != null) {
            params.forEach((key, values) -> Arrays.stream(values).forEach(value -> {
                postParameters.add(new BasicNameValuePair(key, value));
            }));
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameters(postParameters);

            return uriBuilder.build();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static void addAuthKeys(HttpRequestBase request) {
        request.addHeader("Backdoor-Key", TestProperties.BACKDOOR_KEY);
        request.addHeader("CSRF-Key", TestProperties.CSRF_KEY);
    }

    /**
     * Removes and restores given data in the datastore. This method is to be called on test startup.
     *
     * <p>Note:  The data associated with the test accounts have to be <strong>manually</strong> removed by removing the data
     * bundle when a test ends because the test accounts are shared across tests.
     *
     * <p>Test data should never be cleared after test in order to prevent incurring additional datastore costs because the
     * test's data may not be accessed in another test. Also although unlikely in normal conditions, when a test fail to
     * remove data bundle on teardown, another test should have no reason to fail.
     *
     * <p>Another reason not to remove associated data after a test is that in case of test failures, it helps to have the
     * associated data in the datastore to debug the failure.
     *
     * <p>This means that removing the data bundle on startup is not always sufficient because a test only knows how
     * to remove its associated data.
     * This is why some tests would fail when they use the same account and use different data.
     * Extending this method to remove data outside its associated data would introduce
     * unnecessary complications such as extra costs and knowing exactly how much data to remove. Removing too much data
     * would not just incur higher datastore costs but we can make tests unexpectedly pass(fail) when the data is expected to
     * be not present(present) in another test.
     *
     * <p>TODO: Hence, we need to explicitly remove the data bundle in tests on teardown to avoid instability of tests.
     * However, removing the data bundle on teardown manually is not a perfect solution because two tests can concurrently
     * access the same account and their data may get mixed up in the process. This is a major problem we need to address.
     */
    public static String removeAndRestoreDataBundle(DataBundle dataBundle) {
        removeDataBundle(dataBundle);
        ResponseBodyAndCode putRequestOutput =
                executePostRequest(Const.ResourceURIs.DATABUNDLE, null, JsonUtils.toJson(dataBundle));
        return putRequestOutput.responseCode == HttpStatus.SC_OK
                ? Const.StatusCodes.BACKDOOR_STATUS_SUCCESS : Const.StatusCodes.BACKDOOR_STATUS_FAILURE;
    }

    /**
     * Removes given data from the datastore.
     *
     * <p>If given entities have already been deleted, it fails silently.
     */
    public static void removeDataBundle(DataBundle dataBundle) {
        executePutRequest(Const.ResourceURIs.DATABUNDLE, null, JsonUtils.toJson(dataBundle));
    }

    /**
     * Deletes a student from the datastore.
     */
    public static void deleteStudent(String unregUserId) {
        Map<String, String[]> params = new HashMap<>();
        params.put(Const.ParamsNames.STUDENT_ID, new String[] { unregUserId });
        executeDeleteRequest(Const.ResourceURIs.STUDENTS, params);
    }

    /**
     * Deletes a feedback session from the datastore.
     */
    public static void deleteFeedbackSession(String feedbackSession, String courseId) {
        Map<String, String[]> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, new String[] { feedbackSession });
        params.put(Const.ParamsNames.COURSE_ID, new String[] { courseId });
        executeDeleteRequest(Const.ResourceURIs.SESSION, params);
    }

    private static final class ResponseBodyAndCode {

        String responseBody;
        int responseCode;

        ResponseBodyAndCode(String responseBody, int responseCode) {
            this.responseBody = responseBody;
            this.responseCode = responseCode;
        }

    }

}
