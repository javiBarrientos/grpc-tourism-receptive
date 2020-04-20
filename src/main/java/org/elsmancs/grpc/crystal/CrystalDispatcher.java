package org.elsmancs.grpc.crystal;

import java.util.logging.Logger;

import org.elsmancs.grpc.Crystal;
import org.elsmancs.grpc.GuestDispatcher;
import org.elsmancs.grpc.payment.PaymentClient;


public class CrystalDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(CrystalDispatcher.class.getName());

    @Override
    public void dispatch(String cardOwner, String cardNumber) throws Exception {
               
        // Open channel with the server
        CrystalClient crystalClient = CrystalClient.init();
        // Call the gRPC Dispatch to order Crystal
        Crystal crystal = crystalClient.Dispatch(cardOwner, cardNumber);

        // Call the gRPC Pay to pay for the crystal
        if (crystal != null  && PaymentClient.execute(cardOwner, cardNumber, crystal.getFee())) {
            // Call the gRPC to confirm crystal units
            logger.info("Crystal confirmed: " + crystalClient.Confirm(crystal.getUnidades()));
        } else {
            logger.info("No available crytal or no credit");
        }

        // The channel is reused between server calls.
        // Close it when the app finish.
        crystalClient.shutDownChannel();
    }
}