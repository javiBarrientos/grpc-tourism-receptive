package org.elsmancs.grpc;

import java.util.logging.Logger;


class UfosDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(UfosDispatcher.class.getName());

    UfosDispatcher() {}

    @Override
    public void dispatch(String cardOwner, String cardNumber) throws Exception {
        
        // Abrimos canal con el server
        UfosParkClient ufosClient = UfosParkClient.init();
        // Llamada al gRPC Dispatch Card para reservar un UFO
        Ufo ufo = ufosClient.Dispatch(cardOwner, cardNumber);

        // Llamada al gRPC Pay para pagar la reserva
        if (ufo != null  && PaymentClient.execute(cardOwner, cardNumber, ufo.getFee())) {
            logger.info("Aqui llamo al servicio para confirmar reserva UFO");
            // Llamada al gRPC para confirmar ese UFO a esa tarjeta
            System.out.println(ufosClient.AssignUfo(ufo.getId(), ufo.getCardNumber()));
        } else {
            logger.info("No hay UFO o credito");
        }

        // El canal se reutiliza entre llamadas al server
        // Cerrarlo al terminar
        ufosClient.shutDownChannel();
        
    }
}