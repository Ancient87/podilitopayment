package com.podilitopay;

import org.jooby.Jooby;
import java.util.concurrent.atomic.AtomicInteger;
import org.jooby.json.Jackson;
import java.util.ArrayList;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author jooby generator
 */
public class App extends Jooby {


    final HashMap<int, Payment> payments = new HashMap();

    final static String wallet = initWallet();

    {
        use(new Jackson());


        path("/payment", () -> {
            get(req -> { 
                int pid = req.param("pid").intValue();
                return getPayment(pid);
            });

            put(req -> {
                PaymentRequest pr = req.params(PaymentRequest.class);
                Payment p = recordPayment(pr);
                payments.add(p.pid, p);
                return p.getView();
            });
        });

        path("/payment/demo", () -> {
            get(req -> {
                return demo();
            });
        });

    }

    public static void main(final String[] args) {
        run(App::new, args);
    }

    public ArrayList<Payment> demo() {
        ArrayList<Payment> mypayments = new ArrayList<>();
        mypayments.add(new Payment(10));
        mypayments.add(new Payment(20));
        mypayments.add(new Payment(30));

        return mypayments;

    }

    // Make booking and return a booking 
    public Payment recordPayment(PaymentRequest pr) {

        // Write the booking to the DB

        Payment p = new Payment();

        // Return the booking
        return b;
    }

    private class PaymentRequest {
        public int fee;

        public PaymentRequest(int fee) {
            this.fee = fee;
        }
    }

    private class Payment {

        public int pid;
        public int fee;
        boolean paid;

        public Payment(int fee) {
            this.fee = fee;
            this.paid = false;
        }
    }
}
