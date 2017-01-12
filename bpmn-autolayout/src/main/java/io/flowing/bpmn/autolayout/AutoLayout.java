package io.flowing.bpmn.autolayout;

import java.util.concurrent.CountDownLatch;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;

import com.coveo.nashorn_modules.Require;
import com.coveo.nashorn_modules.ResourceFolder;

import jdk.nashorn.api.scripting.NashornScriptEngine;

public class AutoLayout {

  private NashornScriptEngine engine;
  private final CountDownLatch callbackLatch = new CountDownLatch(1);
  private String bpmnResult;

  public AutoLayout() {
    try {
      engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");

      ResourceFolder rootFolder = ResourceFolder.create( //
          getClass().getClassLoader(), //
          "io/flowing/bpmn/autolayout", "UTF-8");
      Require.enable(engine, rootFolder);
    } catch (Exception ex) {
      throw new RuntimeException("Could not configure JavaScript: " + ex.getMessage(), ex);
    }
  }

  public static interface Callback {
    public void call(String xml);
  }

  @SuppressWarnings("restriction")
  public String doAutoLayout(String bpmnXml) {
    try {
//      String javaScript = "function setTimeout(func, milliseconds) {func()};\n" + "function execute(callback) {\n" //
//          + "var AutoLayout = require('./index');\n" //
//          + "var xmlWithoutDi = '" + bpmnXml + "';\n" //
//          + "var autoLayout = new AutoLayout();\n" //
//          + "autoLayout.layoutProcess(xmlWithoutDi, callback);\n" //
//          + "return xmlWithoutDi;" + "}";
      String javaScript = //
          "  function setTimeout(func, milliseconds) {func()};\n" //
          + "function execute(xmlWithoutDi, callback) {\n" //
          + "  var AutoLayout = require('./lib/AutoLayout');\n"
          + "  new AutoLayout().layoutProcess(xmlWithoutDi, callback);\n"
          + "}";

      engine.eval(javaScript);
      engine.invokeFunction("execute", bpmnXml, new Callback() {
        public void call(String xml) {
          bpmnResult = xml;
          callbackLatch.countDown();
        }
      });
      callbackLatch.await();
      return bpmnResult;
    } catch (Exception ex) {
      throw new RuntimeException("Could not run JavaScript: " + ex.getMessage(), ex);
    }

  }

  public static void main(String[] args) {
//    System.out.println(new AutoLayout().doAutoLayout(
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" id=\"Definitions_1\" targetNamespace=\"http://bpmn.io/schema/bpmn\"><bpmn:process id=\"Process_1\" isExecutable=\"false\"><bpmn:startEvent id=\"StartEvent_1\" /></bpmn:process></bpmn:definitions>"));

    String x = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + "<definitions xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" id=\"definitions_42a52a42-ed8b-4ece-b61f-e4b79dedb98d\" targetNamespace=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "  <process id=\"Process_OrderPlaced\" isExecutable=\"true\">\n" + "    <startEvent id=\"startEvent_cdca2e21-9e0e-492e-ad1e-6570d1fdd8c4\">\n" + "      <outgoing>sequenceFlow_81b75e8a-2ce0-46bd-8e16-24fa1c88854c</outgoing>\n" + "    </startEvent>\n" + "    <serviceTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.ExecuteLogicAdapter\" id=\"ServiceTask_CreateOrder_1\" name=\"CreateOrder\">\n" + "      <incoming>sequenceFlow_81b75e8a-2ce0-46bd-8e16-24fa1c88854c</incoming>\n" + "      <outgoing>sequenceFlow_f8212a76-f068-4f81-87ed-6f1ec24ce654</outgoing>\n" + "    </serviceTask>\n" + "    <sequenceFlow id=\"sequenceFlow_81b75e8a-2ce0-46bd-8e16-24fa1c88854c\" sourceRef=\"startEvent_cdca2e21-9e0e-492e-ad1e-6570d1fdd8c4\" targetRef=\"ServiceTask_CreateOrder_1\"/>\n" + "    <sendTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter\" id=\"SendTask_OrderCreated_2\" name=\"OrderCreated\">\n" + "      <extensionElements>\n" + "        <camunda:field name=\"type\">\n" + "          <camunda:string>Event</camunda:string>\n" + "        </camunda:field>\n" + "        <camunda:field name=\"name\">\n" + "          <camunda:string>OrderCreated</camunda:string>\n" + "        </camunda:field>\n" + "      </extensionElements>\n" + "      <incoming>sequenceFlow_f8212a76-f068-4f81-87ed-6f1ec24ce654</incoming>\n" + "      <outgoing>sequenceFlow_9f8c9619-b90f-4fcb-b1ce-b445947683c5</outgoing>\n" + "    </sendTask>\n" + "    <sequenceFlow id=\"sequenceFlow_f8212a76-f068-4f81-87ed-6f1ec24ce654\" sourceRef=\"ServiceTask_CreateOrder_1\" targetRef=\"SendTask_OrderCreated_2\"/>\n" + "    <sendTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter\" id=\"SendTask_ReserveGoods_3\" name=\"ReserveGoods\">\n" + "      <extensionElements>\n" + "        <camunda:field name=\"type\">\n" + "          <camunda:string>Command</camunda:string>\n" + "        </camunda:field>\n" + "        <camunda:field name=\"name\">\n" + "          <camunda:string>ReserveGoods</camunda:string>\n" + "        </camunda:field>\n" + "      </extensionElements>\n" + "      <incoming>sequenceFlow_9f8c9619-b90f-4fcb-b1ce-b445947683c5</incoming>\n" + "      <outgoing>sequenceFlow_bb761387-c37b-4ab7-af17-1eb094e0402f</outgoing>\n" + "    </sendTask>\n" + "    <sequenceFlow id=\"sequenceFlow_9f8c9619-b90f-4fcb-b1ce-b445947683c5\" sourceRef=\"SendTask_OrderCreated_2\" targetRef=\"SendTask_ReserveGoods_3\"/>\n" + "    <sendTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter\" id=\"SendTask_DoPayment_4\" name=\"DoPayment\">\n" + "      <extensionElements>\n" + "        <camunda:field name=\"type\">\n" + "          <camunda:string>Command</camunda:string>\n" + "        </camunda:field>\n" + "        <camunda:field name=\"name\">\n" + "          <camunda:string>DoPayment</camunda:string>\n" + "        </camunda:field>\n" + "      </extensionElements>\n" + "      <incoming>sequenceFlow_bb761387-c37b-4ab7-af17-1eb094e0402f</incoming>\n" + "      <outgoing>sequenceFlow_d5525a28-08a6-4976-b7d1-afae8a7e5ffd</outgoing>\n" + "    </sendTask>\n" + "    <sequenceFlow id=\"sequenceFlow_bb761387-c37b-4ab7-af17-1eb094e0402f\" sourceRef=\"SendTask_ReserveGoods_3\" targetRef=\"SendTask_DoPayment_4\"/>\n" + "    <receiveTask id=\"ReceiveTask_GoodsReserved_5\" messageRef=\"message_e9d00715-4b6a-4e92-8897-80695d52dc67\" name=\"GoodsReserved\">\n" + "      <incoming>sequenceFlow_d5525a28-08a6-4976-b7d1-afae8a7e5ffd</incoming>\n" + "      <outgoing>sequenceFlow_0731ce7a-d47a-448f-a400-81f87e173954</outgoing>\n" + "    </receiveTask>\n" + "    <sequenceFlow id=\"sequenceFlow_d5525a28-08a6-4976-b7d1-afae8a7e5ffd\" sourceRef=\"SendTask_DoPayment_4\" targetRef=\"ReceiveTask_GoodsReserved_5\"/>\n" + "    <receiveTask id=\"ReceiveTask_PaymentReceived_6\" messageRef=\"message_36ff0a28-0f2f-49f7-a7c9-1c9a7e160bee\" name=\"PaymentReceived\">\n" + "      <incoming>sequenceFlow_0731ce7a-d47a-448f-a400-81f87e173954</incoming>\n" + "      <outgoing>sequenceFlow_11512066-0e2a-4739-aa98-5032b536e585</outgoing>\n" + "    </receiveTask>\n" + "    <sequenceFlow id=\"sequenceFlow_0731ce7a-d47a-448f-a400-81f87e173954\" sourceRef=\"ReceiveTask_GoodsReserved_5\" targetRef=\"ReceiveTask_PaymentReceived_6\"/>\n" + "    <parallelGateway id=\"fork-7\">\n" + "      <incoming>sequenceFlow_11512066-0e2a-4739-aa98-5032b536e585</incoming>\n" + "      <outgoing>sequenceFlow_bf170ee5-787c-470c-91c4-fa20b2d6bb1a</outgoing>\n" + "      <outgoing>sequenceFlow_5ddec9e3-cf49-4cb8-a8bd-b259de338d3e</outgoing>\n" + "    </parallelGateway>\n" + "    <sequenceFlow id=\"sequenceFlow_11512066-0e2a-4739-aa98-5032b536e585\" sourceRef=\"ReceiveTask_PaymentReceived_6\" targetRef=\"fork-7\"/>\n" + "    <receiveTask id=\"ReceiveTask_GoodsReserved_8\" messageRef=\"message_e9d00715-4b6a-4e92-8897-80695d52dc67\" name=\"GoodsReserved\">\n" + "      <incoming>sequenceFlow_bf170ee5-787c-470c-91c4-fa20b2d6bb1a</incoming>\n" + "      <outgoing>sequenceFlow_4aab180e-fbfb-4381-a34c-a07172029dfc</outgoing>\n" + "    </receiveTask>\n" + "    <sequenceFlow id=\"sequenceFlow_bf170ee5-787c-470c-91c4-fa20b2d6bb1a\" sourceRef=\"fork-7\" targetRef=\"ReceiveTask_GoodsReserved_8\"/>\n" + "    <parallelGateway id=\"join-7\">\n" + "      <incoming>sequenceFlow_4aab180e-fbfb-4381-a34c-a07172029dfc</incoming>\n" + "      <incoming>sequenceFlow_eebd5ea6-e556-42bf-8626-db91f1ed4b59</incoming>\n" + "      <outgoing>sequenceFlow_9c12ddef-7530-4731-8385-5c9effa7080a</outgoing>\n" + "    </parallelGateway>\n" + "    <sequenceFlow id=\"sequenceFlow_4aab180e-fbfb-4381-a34c-a07172029dfc\" sourceRef=\"ReceiveTask_GoodsReserved_8\" targetRef=\"join-7\"/>\n" + "    <receiveTask id=\"ReceiveTask_PaymentReceived_9\" messageRef=\"message_36ff0a28-0f2f-49f7-a7c9-1c9a7e160bee\" name=\"PaymentReceived\">\n" + "      <incoming>sequenceFlow_5ddec9e3-cf49-4cb8-a8bd-b259de338d3e</incoming>\n" + "      <outgoing>sequenceFlow_eebd5ea6-e556-42bf-8626-db91f1ed4b59</outgoing>\n" + "    </receiveTask>\n" + "    <sequenceFlow id=\"sequenceFlow_5ddec9e3-cf49-4cb8-a8bd-b259de338d3e\" sourceRef=\"fork-7\" targetRef=\"ReceiveTask_PaymentReceived_9\"/>\n" + "    <sequenceFlow id=\"sequenceFlow_eebd5ea6-e556-42bf-8626-db91f1ed4b59\" sourceRef=\"ReceiveTask_PaymentReceived_9\" targetRef=\"join-7\"/>\n" + "    <sendTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter\" id=\"SendTask_PickGoods_10\" name=\"PickGoods\">\n" + "      <extensionElements>\n" + "        <camunda:field name=\"type\">\n" + "          <camunda:string>Command</camunda:string>\n" + "        </camunda:field>\n" + "        <camunda:field name=\"name\">\n" + "          <camunda:string>PickGoods</camunda:string>\n" + "        </camunda:field>\n" + "      </extensionElements>\n" + "      <incoming>sequenceFlow_9c12ddef-7530-4731-8385-5c9effa7080a</incoming>\n" + "      <outgoing>sequenceFlow_6bfd68d0-4e82-4d44-91dd-3b1a73c396c6</outgoing>\n" + "    </sendTask>\n" + "    <sequenceFlow id=\"sequenceFlow_9c12ddef-7530-4731-8385-5c9effa7080a\" sourceRef=\"join-7\" targetRef=\"SendTask_PickGoods_10\"/>\n" + "    <receiveTask id=\"ReceiveTask_GoodsPicked_11\" messageRef=\"message_5bb027f0-d642-429c-bbc7-05bcfe9c5b1b\" name=\"GoodsPicked\">\n" + "      <incoming>sequenceFlow_6bfd68d0-4e82-4d44-91dd-3b1a73c396c6</incoming>\n" + "      <outgoing>sequenceFlow_7869454c-93f8-4c45-9df3-0ad24cbd9ce3</outgoing>\n" + "    </receiveTask>\n" + "    <sequenceFlow id=\"sequenceFlow_6bfd68d0-4e82-4d44-91dd-3b1a73c396c6\" sourceRef=\"SendTask_PickGoods_10\" targetRef=\"ReceiveTask_GoodsPicked_11\"/>\n" + "    <sendTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter\" id=\"SendTask_ShipGoods_12\" name=\"ShipGoods\">\n" + "      <extensionElements>\n" + "        <camunda:field name=\"type\">\n" + "          <camunda:string>Command</camunda:string>\n" + "        </camunda:field>\n" + "        <camunda:field name=\"name\">\n" + "          <camunda:string>ShipGoods</camunda:string>\n" + "        </camunda:field>\n" + "      </extensionElements>\n" + "      <incoming>sequenceFlow_7869454c-93f8-4c45-9df3-0ad24cbd9ce3</incoming>\n" + "      <outgoing>sequenceFlow_7b7291cf-08e5-4718-ab90-64628664ff05</outgoing>\n" + "    </sendTask>\n" + "    <sequenceFlow id=\"sequenceFlow_7869454c-93f8-4c45-9df3-0ad24cbd9ce3\" sourceRef=\"ReceiveTask_GoodsPicked_11\" targetRef=\"SendTask_ShipGoods_12\"/>\n" + "    <receiveTask id=\"ReceiveTask_ShipmentShipped_13\" messageRef=\"message_dc6f44d2-b6f0-4cf4-be5d-f8e368b1960c\" name=\"ShipmentShipped\">\n" + "      <incoming>sequenceFlow_7b7291cf-08e5-4718-ab90-64628664ff05</incoming>\n" + "      <outgoing>sequenceFlow_94864cc8-429c-4e7f-9c89-63687389d62d</outgoing>\n" + "    </receiveTask>\n" + "    <sequenceFlow id=\"sequenceFlow_7b7291cf-08e5-4718-ab90-64628664ff05\" sourceRef=\"SendTask_ShipGoods_12\" targetRef=\"ReceiveTask_ShipmentShipped_13\"/>\n" + "    <sendTask camunda:class=\"io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter\" id=\"SendTask_OrderCompleted_14\" name=\"OrderCompleted\">\n" + "      <extensionElements>\n" + "        <camunda:field name=\"type\">\n" + "          <camunda:string>Event</camunda:string>\n" + "        </camunda:field>\n" + "        <camunda:field name=\"name\">\n" + "          <camunda:string>OrderCompleted</camunda:string>\n" + "        </camunda:field>\n" + "      </extensionElements>\n" + "      <incoming>sequenceFlow_94864cc8-429c-4e7f-9c89-63687389d62d</incoming>\n" + "      <outgoing>sequenceFlow_9dbd86a5-23f9-44d5-b7ab-40259600ebb7</outgoing>\n" + "    </sendTask>\n" + "    <sequenceFlow id=\"sequenceFlow_94864cc8-429c-4e7f-9c89-63687389d62d\" sourceRef=\"ReceiveTask_ShipmentShipped_13\" targetRef=\"SendTask_OrderCompleted_14\"/>\n" + "    <endEvent id=\"endEvent_4c1dbcf5-ac92-431b-a802-4c60a4dac2d9\">\n" + "      <incoming>sequenceFlow_9dbd86a5-23f9-44d5-b7ab-40259600ebb7</incoming>\n" + "    </endEvent>\n" + "    <sequenceFlow id=\"sequenceFlow_9dbd86a5-23f9-44d5-b7ab-40259600ebb7\" sourceRef=\"SendTask_OrderCompleted_14\" targetRef=\"endEvent_4c1dbcf5-ac92-431b-a802-4c60a4dac2d9\"/>\n" + "  </process>\n" + "  <message id=\"message_e9d00715-4b6a-4e92-8897-80695d52dc67\" name=\"GoodsReserved\"/>\n" + "  <message id=\"message_36ff0a28-0f2f-49f7-a7c9-1c9a7e160bee\" name=\"PaymentReceived\"/>\n" + "  <message id=\"message_5bb027f0-d642-429c-bbc7-05bcfe9c5b1b\" name=\"GoodsPicked\"/>\n" + "  <message id=\"message_dc6f44d2-b6f0-4cf4-be5d-f8e368b1960c\" name=\"ShipmentShipped\"/>\n" + "</definitions>";
    System.out.println(new AutoLayout().doAutoLayout(x));
  }
}
