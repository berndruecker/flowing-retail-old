   // issue ReserveGoodsCommand    
   // issue DoPaymentCommand
    
    // wait for occurrence of the events:
    // - GoodsReservedEvent
    // - PaymentReceivedEvent
    // or some error message, in which case we have to cleanup
    
    // issue PickGoodsCommand
    // wait for occurrence of the events:
    // - GoodsPickedEvent
    
    // issue ShipCommand
    // wait for occurrence of the events:
    // - ShipmentShippedEvent
    
    
 -> Error on Payment
 -> Timeout on Payment