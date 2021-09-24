package org.example.fhir.intermediate;

import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.example.fhir.intermediate.TimeInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class TimeInterceptorTest {

    private TimeInterceptor timeInterceptor;

    @Before
    public void setUp() throws Exception {
        timeInterceptor = new TimeInterceptor();
    }

    @Test
    public void interceptRequestTest() {
        timeInterceptor.interceptRequest(Mockito.mock(IHttpRequest.class));
        final long elapsedTime = timeInterceptor.getElapsedTimeAndRestart();
        Assert.assertEquals("elapsedTime should be 0", 0, elapsedTime);
    }

    @Test(expected = IllegalStateException.class)
    public void interceptResponseTest() throws IOException {
        timeInterceptor.interceptResponse(Mockito.mock(IHttpResponse.class));
    }

    @Test
    public void getElapsedTimeAndRestartTest() throws IOException {
        timeInterceptor.interceptRequest(Mockito.mock(IHttpRequest.class));
        timeInterceptor.interceptResponse(Mockito.mock(IHttpResponse.class));

        final long time1 = timeInterceptor.getElapsedTimeAndRestart();

        final long time2 = timeInterceptor.getElapsedTimeAndRestart();

        Assert.assertNotEquals("time1 should be not equal to time2", time1, time2);
        Assert.assertTrue("time1 should be bigger than 0 (time1 was started)", time1 > 0);
        Assert.assertEquals("time2 should be 0 (ensures that StopWatch was did reset)", 0, time2);
    }
}