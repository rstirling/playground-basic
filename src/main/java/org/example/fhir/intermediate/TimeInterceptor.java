package org.example.fhir.intermediate;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TimeInterceptor implements IClientInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TimeInterceptor.class);

    private StopWatch stopWatch;
    private long elapsedTime = 0;

    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        stopWatch = new StopWatch();
        stopWatch.startTask("getTimes");
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException, IllegalStateException {
        if(stopWatch == null){
            final IllegalStateException illegalStateException = new IllegalStateException("Unable to calculate response time without a prior request.");
            log.error("Error getting response time: ", illegalStateException);
            throw illegalStateException;
        }
        final long millisEnd = stopWatch.getMillisAndRestart();
        elapsedTime = millisEnd;
        log.info("Response time[{}]ms", millisEnd);
    }

    public long getElapsedTimeAndRestart() {
        long time = this.elapsedTime;
        this.elapsedTime = 0;
        return time;
    }
}
