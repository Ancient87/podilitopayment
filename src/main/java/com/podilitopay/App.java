package com.podilito;

import org.jooby.Jooby;
import java.util.concurrent.atomic.AtomicInteger;
import org.jooby.json.Jackson;
import java.util.ArrayList;
import java.util.HashMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.radixdlt.client.assets.Asset;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.address.RadixAddress;
import com.radixdlt.client.core.identity.EncryptedRadixIdentity;
import com.radixdlt.client.core.identity.RadixIdentity;
import com.radixdlt.client.core.network.AtomSubmissionUpdate;
import com.radixdlt.client.core.network.AtomSubmissionUpdate.AtomSubmissionState;
import com.radixdlt.client.messaging.RadixMessaging;
import com.radixdlt.client.wallet.RadixWallet;
import io.reactivex.Completable;


/**
 * @author jooby generator
 */
public class App extends Jooby {


    final HashMap<Integer, Payment> payments = new HashMap();

    final RadixIdentity rdx = initRdx();

    final static AtomicInteger idgen = new AtomicInteger();
    
    {
        this.subscribe();
        use(new Jackson());


        path("/payment", () -> {
            get(req -> { 
                int pid = req.param("pid").intValue();
                return getPayment(pid);
            });

            put(req -> {
                PaymentRequest pr = req.params(PaymentRequest.class);
                Payment p = recordPayment(pr);
                payments.put(p.pid, p);
                return p;
            });
        });

        path("/payment/demo", () -> {
            get(req -> {
                return demo();
            });
        });

    }

    public static void main(final String[] args) {
        RadixUniverse.bootstrap(Bootstrap.valueOf("ALPHANET".toUpperCase()));
        RadixUniverse.getInstance().getNetwork().getStatusUpdates().subscribe(System.out::println);      
        run(App::new, args);
    }

    public ArrayList<Payment> demo() {
        ArrayList<Payment> mypayments = new ArrayList<>();
        mypayments.add(new Payment(10));
        mypayments.add(new Payment(20));
        mypayments.add(new Payment(30));

        return mypayments;

    }

    public Payment getPayment(Integer pid) {
        return payments.get(pid);
    }

    public void subscribe() {
        RadixWallet.getInstance()
            .getXRDTransactions(getRadixAddress(rdx))
            .subscribe(transaction -> {
                // Save sender address: money
                // If someone sends you more money, just add to the previous
                // If cap reached -> select winner & notify losers + send transaction to the winner
                long amount = transaction.getAmount();
                System.out.println("Amount: " + amount);
            });
            return;
        }

            // Make booking and return a booking 
            public Payment recordPayment(PaymentRequest pr) {

                // Write the booking to the DB

                Payment p = new Payment(pr.fee);

                // Return the booking
                return p;
            }

            public RadixIdentity initRdx() {
                RadixIdentity rdx = null;
                try {
                    rdx = new EncryptedRadixIdentity("podilito", "podoloito");
                    RadixAddress sourceAddress = getRadixAddress(rdx); 
                    System.out.println("Address: " + sourceAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return rdx;
            }

            private RadixAddress getRadixAddress(RadixIdentity rdx) {
                return RadixUniverse.getInstance().getAddressFrom(rdx.getPublicKey());
            }



            private class Payment {

                public int pid;
                public int fee;
                boolean paid;

                public Payment(int fee) {
                    this.fee = fee;
                    this.paid = false;
                    this.pid = idgen.incrementAndGet();
                }
            }
    }
