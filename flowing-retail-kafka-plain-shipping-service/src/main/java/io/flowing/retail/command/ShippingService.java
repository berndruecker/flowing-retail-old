package io.flowing.retail.command;

public interface ShippingService {

  /**
   * 
   * @param pick id - required to identify the pile of goods to be packed in the parcel
   * @param recipient name
   * @param complete address the shipment is sent to
   * @param logisticsProvider delivering the shipment (e.g. DHL, UPS, ...)
   * @return shipment id created (also printed on the label of the parcel)
   */
  public String createShipment(String pickId, String recipientName, String recipientAddress, String logisticsProvider);

}
