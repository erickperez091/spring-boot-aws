package com.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

public class SQSQueueHandler implements RequestHandler< SQSEvent, Void > {

    @Override
    public Void handleRequest( SQSEvent sqsEvent, Context context ) {
        sqsEvent.getRecords().forEach( record -> {
            System.out.printf( "This is the body from SQS Record: %s%n",record.getBody() );
        } );
        return null;
    }
}
