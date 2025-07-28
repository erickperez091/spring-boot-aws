package com.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.example.entity.Record;

public class SQSQueueHandler implements RequestHandler< SQSEvent, Void > {

    @Override
    public Void handleRequest( SQSEvent sqsEvent, Context context ) {
        sqsEvent.getRecords().forEach( record -> {
            Record rec = new Record( String.format( "This is the body from SQS Record: %s%n", record.getBody() ) );
            System.out.printf(rec.toString());
        } );
        return null;
    }
}
