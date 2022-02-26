import com.hedera.hashgraph.sdk.*;

import java.util.concurrent.TimeoutException;

public class PSDropServiceEngine implements HaderaConnection {
    public static void main(String[] args){

        //create topic and get the id
        TopicId tid = null;
        try {
            tid = HaderaConnection.createTopic();
        } catch (PrecheckStatusException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ReceiptStatusException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 50; ++i){
            String msg = String.format("This is message number: \t%d\t in the topic \t%s", i, tid);
            TransactionResponse submitMessage = null;
            try {
                submitMessage = HaderaConnection.submitMessage(tid, msg);
                //Get the receipt of the transaction
                TransactionReceipt msgReceipt = submitMessage.getReceipt(client);
                System.out.println(msgReceipt);
            } catch (ReceiptStatusException e) {
                e.printStackTrace();
            } catch (PrecheckStatusException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

}
