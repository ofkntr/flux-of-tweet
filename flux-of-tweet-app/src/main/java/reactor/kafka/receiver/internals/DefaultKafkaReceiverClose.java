package reactor.kafka.receiver.internals;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultKafkaReceiverClose {

    public void close(DefaultKafkaReceiver defaultKafkaReceiver){
        try{
            defaultKafkaReceiver.close();
        }catch(Exception e){
            log.error("Exception when closing kafka consumer",e);
        }
    }

}
