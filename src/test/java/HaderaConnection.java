import com.hedera.hashgraph.sdk.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public interface HaderaConnection {

    Client client = connect();

    static Client connect(){

        //Grab your Hedera testnet account ID and private key
        AccountId myAccountId = AccountId.fromString(System.getenv("MY_ACCOUNT_ID"));
        PrivateKey myPrivateKey = PrivateKey.fromString(System.getenv("MY_PRIVATE_KEY"));

        //Build your Hedera client
        Client client = Client.forTestnet();
        client.setOperator(myAccountId, myPrivateKey);

        return client;
    }

    static void printAccountBalance(AccountId myAccountId) throws PrecheckStatusException, TimeoutException {
        AccountBalance accountBalance = new AccountBalanceQuery().setAccountId(myAccountId).execute(client);
        System.out.println("The account balance is: "+accountBalance.hbars);
    }

    static void subscribeTopic(TopicId tid){
        //Subscribe to the topic
        new TopicMessageQuery()
                .setTopicId(tid)
                .subscribe(client, resp -> {
                    String messageAsString = new String(resp.contents, StandardCharsets.UTF_8);
                    System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
                });
    }

    static TransactionResponse submitMessage(TopicId tid, String msg) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        //Submit a message to a topic
        TransactionResponse submitMessage = new TopicMessageSubmitTransaction()
                .setTopicId(tid)
                .setMessage(msg)
                .execute(client);
        //TransactionRecord record = submitMessage.getRecord(client);
        //System.out.println("The transaction for record "+i+" is " +record);
        return submitMessage;
    }

    static TopicId createTopic() throws PrecheckStatusException, TimeoutException, ReceiptStatusException, InterruptedException {

        //Create a new topic
        TransactionResponse txResponse = new TopicCreateTransaction()
                .execute(client);

        //Get the receipt
        TransactionReceipt receipt = txResponse.getReceipt(client);

        //Get the topic ID
        TopicId topicId = receipt.topicId;

        //Log the topic ID
        System.out.println("Your topic ID is: " +topicId);

        // Wait 5 seconds between consensus topic creation and subscription creation
        Thread.sleep(5000);

        return topicId;
    }

}
