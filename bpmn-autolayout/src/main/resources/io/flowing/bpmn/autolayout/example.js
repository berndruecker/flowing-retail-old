var AutoLayout = require('./index');

var xmlWithoutDi = '<?xml version="1.0" encoding="UTF-8"?>' +
'<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn" exporter="Camunda Modeler" exporterVersion="0.7.0-dev">' +
                        '<bpmn:process id="Process_1" isExecutable="false">'+
                          '<bpmn:startEvent id="StartEvent_1">' +
                            '<bpmn:outgoing>SequenceFlow_1e8q2t2</bpmn:outgoing>' +
                            '<bpmn:outgoing>SequenceFlow_1taw05v</bpmn:outgoing>' +
                          '</bpmn:startEvent>' +
                          '<bpmn:task id="Task_13vu0q1">' +
                            '<bpmn:incoming>SequenceFlow_1e8q2t2</bpmn:incoming>' +
                            '<bpmn:outgoing>SequenceFlow_1gi0q3c</bpmn:outgoing>' +
                          '</bpmn:task>' +
                          '<bpmn:sequenceFlow id="SequenceFlow_1e8q2t2" sourceRef="StartEvent_1" targetRef="Task_13vu0q1" />' +
                          '<bpmn:endEvent id="EndEvent_0ei7qv4">' +
                            '<bpmn:incoming>SequenceFlow_1gi0q3c</bpmn:incoming>' +
                          '</bpmn:endEvent>' +
                          '<bpmn:sequenceFlow id="SequenceFlow_1gi0q3c" sourceRef="Task_13vu0q1" targetRef="EndEvent_0ei7qv4" />' +
                          '<bpmn:endEvent id="EndEvent_15m6igf">' +
                            '<bpmn:incoming>SequenceFlow_1taw05v</bpmn:incoming>' +
                          '</bpmn:endEvent>' +
                          '<bpmn:sequenceFlow id="SequenceFlow_1taw05v" sourceRef="StartEvent_1" targetRef="EndEvent_15m6igf" />' +
                        '</bpmn:process>' +
                      '</bpmn:definitions>';



xmlWithoutDi = '<?xml version="1.0" encoding="UTF-8"?>'
//+'<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn" exporter="Camunda Modeler" exporterVersion="0.7.0-dev">'
//+'<bpmn:definitions xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="definitions_8eb3244e-21cb-4b73-9743-6bc37cea1f5d" targetNamespace="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL">'
//+'<bpmn:definitions xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"            xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="definitions_8eb3244e-21cb-4b73-9743-6bc37cea1f5d" targetNamespace="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL">'
 + '<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="definitions_d5f8e99e-69e3-486c-a4fe-c1726f3b903e" targetNamespace="http://io.flowing/sample" xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL">' 
 + '  <bpmn:process id="process_fee641dd-e591-40b3-b4d9-a30d61de2504">' 
 + '    <bpmn:startEvent id="startEvent_ca9c8341-cad8-4968-86ff-ba9982ecc275">' 
 + '      <bpmn:outgoing>sequenceFlow_511237a1-4e89-4c89-8ec3-331e6a1a9172</bpmn:outgoing>' 
 + '    </bpmn:startEvent>' 
 + '    <bpmn:serviceTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.ExecuteLogicAdapter" id="ServiceTask_CreateOrder_1" name="CreateOrder">' 
 + '      <bpmn:incoming>sequenceFlow_511237a1-4e89-4c89-8ec3-331e6a1a9172</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_edd4c474-3947-4b5e-87d1-002bef6a5f71</bpmn:outgoing>' 
 + '    </bpmn:serviceTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_511237a1-4e89-4c89-8ec3-331e6a1a9172" sourceRef="startEvent_ca9c8341-cad8-4968-86ff-ba9982ecc275" targetRef="ServiceTask_CreateOrder_1"/>' 
 + '    <bpmn:sendTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter" id="SendTask_OrderCreated_2" name="OrderCreated">' 
 + '      <bpmn:extensionElements>' 
 + '        <camunda:field name="type">' 
 + '          <camunda:string>Event</camunda:string>' 
 + '        </camunda:field>' 
 + '        <camunda:field name="name">' 
 + '          <camunda:string>OrderCreated</camunda:string>' 
 + '        </camunda:field>' 
 + '      </bpmn:extensionElements>' 
 + '      <bpmn:incoming>sequenceFlow_edd4c474-3947-4b5e-87d1-002bef6a5f71</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_55bc6314-4351-47ea-bb8a-400c37feb18d</bpmn:outgoing>' 
 + '    </bpmn:sendTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_edd4c474-3947-4b5e-87d1-002bef6a5f71" sourceRef="ServiceTask_CreateOrder_1" targetRef="SendTask_OrderCreated_2"/>' 
 + '    <bpmn:sendTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter" id="SendTask_ReserveGoods_3" name="ReserveGoods">' 
 + '      <bpmn:extensionElements>' 
 + '        <camunda:field name="type">' 
 + '          <camunda:string>Command</camunda:string>' 
 + '        </camunda:field>' 
 + '        <camunda:field name="name">' 
 + '          <camunda:string>ReserveGoods</camunda:string>' 
 + '        </camunda:field>' 
 + '      </bpmn:extensionElements>' 
 + '      <bpmn:incoming>sequenceFlow_55bc6314-4351-47ea-bb8a-400c37feb18d</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_832013a2-b0e0-4b3e-9b21-7fa076afa606</bpmn:outgoing>' 
 + '    </bpmn:sendTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_55bc6314-4351-47ea-bb8a-400c37feb18d" sourceRef="SendTask_OrderCreated_2" targetRef="SendTask_ReserveGoods_3"/>' 
 + '    <bpmn:sendTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter" id="SendTask_DoPayment_4" name="DoPayment">' 
 + '      <bpmn:extensionElements>' 
 + '        <camunda:field name="type">' 
 + '          <camunda:string>Command</camunda:string>' 
 + '        </camunda:field>' 
 + '        <camunda:field name="name">' 
 + '          <camunda:string>DoPayment</camunda:string>' 
 + '        </camunda:field>' 
 + '      </bpmn:extensionElements>' 
 + '      <bpmn:incoming>sequenceFlow_832013a2-b0e0-4b3e-9b21-7fa076afa606</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_7c2cec90-4253-4be4-ba67-55542da3c53b</bpmn:outgoing>' 
 + '    </bpmn:sendTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_832013a2-b0e0-4b3e-9b21-7fa076afa606" sourceRef="SendTask_ReserveGoods_3" targetRef="SendTask_DoPayment_4"/>' 
 + '    <bpmn:receiveTask id="ReceiveTask_GoodsReserved_5" messageRef="message_1023f062-44c8-4388-86c6-811c31e1b0eb" name="GoodsReserved">' 
 + '      <bpmn:incoming>sequenceFlow_7c2cec90-4253-4be4-ba67-55542da3c53b</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_a89fb23e-eee5-4449-8ebd-2737f649c311</bpmn:outgoing>' 
 + '    </bpmn:receiveTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_7c2cec90-4253-4be4-ba67-55542da3c53b" sourceRef="SendTask_DoPayment_4" targetRef="ReceiveTask_GoodsReserved_5"/>' 
 + '    <bpmn:receiveTask id="ReceiveTask_PaymentReceived_6" messageRef="message_de5acff0-f2fb-4720-9ef1-e8eb5ea22795" name="PaymentReceived">' 
 + '      <bpmn:incoming>sequenceFlow_a89fb23e-eee5-4449-8ebd-2737f649c311</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_1611238d-886f-489a-931d-80bc9e162d5e</bpmn:outgoing>' 
 + '    </bpmn:receiveTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_a89fb23e-eee5-4449-8ebd-2737f649c311" sourceRef="ReceiveTask_GoodsReserved_5" targetRef="ReceiveTask_PaymentReceived_6"/>' 
 + '    <bpmn:parallelGateway id="fork-7">' 
 + '      <bpmn:incoming>sequenceFlow_1611238d-886f-489a-931d-80bc9e162d5e</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_53cb9cc2-c214-4733-8035-0c67bfee41fe</bpmn:outgoing>' 
 + '      <bpmn:outgoing>sequenceFlow_c65a4cd9-cdb8-4147-8aff-62743389c3f8</bpmn:outgoing>' 
 + '    </bpmn:parallelGateway>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_1611238d-886f-489a-931d-80bc9e162d5e" sourceRef="ReceiveTask_PaymentReceived_6" targetRef="fork-7"/>' 
 + '    <bpmn:receiveTask id="ReceiveTask_GoodsReserved_8" messageRef="message_1023f062-44c8-4388-86c6-811c31e1b0eb" name="GoodsReserved">' 
 + '      <bpmn:incoming>sequenceFlow_53cb9cc2-c214-4733-8035-0c67bfee41fe</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_3879c6c0-dbba-41d7-96d3-b9e562013e47</bpmn:outgoing>' 
 + '    </bpmn:receiveTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_53cb9cc2-c214-4733-8035-0c67bfee41fe" sourceRef="fork-7" targetRef="ReceiveTask_GoodsReserved_8"/>' 
 + '    <bpmn:parallelGateway id="join-7">' 
 + '      <bpmn:incoming>sequenceFlow_3879c6c0-dbba-41d7-96d3-b9e562013e47</bpmn:incoming>' 
 + '      <bpmn:incoming>sequenceFlow_e337db96-d8d0-4eeb-b2b3-3bd8fdb4cbf5</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_92fe13eb-bbc7-4397-9a6d-a1963eb2d9fc</bpmn:outgoing>' 
 + '    </bpmn:parallelGateway>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_3879c6c0-dbba-41d7-96d3-b9e562013e47" sourceRef="ReceiveTask_GoodsReserved_8" targetRef="join-7"/>' 
 + '    <bpmn:receiveTask id="ReceiveTask_PaymentReceived_9" messageRef="message_de5acff0-f2fb-4720-9ef1-e8eb5ea22795" name="PaymentReceived">' 
 + '      <bpmn:incoming>sequenceFlow_c65a4cd9-cdb8-4147-8aff-62743389c3f8</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_e337db96-d8d0-4eeb-b2b3-3bd8fdb4cbf5</bpmn:outgoing>' 
 + '    </bpmn:receiveTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_c65a4cd9-cdb8-4147-8aff-62743389c3f8" sourceRef="fork-7" targetRef="ReceiveTask_PaymentReceived_9"/>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_e337db96-d8d0-4eeb-b2b3-3bd8fdb4cbf5" sourceRef="ReceiveTask_PaymentReceived_9" targetRef="join-7"/>' 
 + '    <bpmn:sendTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter" id="SendTask_PickGoods_10" name="PickGoods">' 
 + '      <bpmn:extensionElements>' 
 + '        <camunda:field name="type">' 
 + '          <camunda:string>Command</camunda:string>' 
 + '        </camunda:field>' 
 + '        <camunda:field name="name">' 
 + '          <camunda:string>PickGoods</camunda:string>' 
 + '        </camunda:field>' 
 + '      </bpmn:extensionElements>' 
 + '      <bpmn:incoming>sequenceFlow_92fe13eb-bbc7-4397-9a6d-a1963eb2d9fc</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_18d8a3bc-87d8-47b6-a7b9-9e67dc411f22</bpmn:outgoing>' 
 + '    </bpmn:sendTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_92fe13eb-bbc7-4397-9a6d-a1963eb2d9fc" sourceRef="join-7" targetRef="SendTask_PickGoods_10"/>' 
 + '    <bpmn:receiveTask id="ReceiveTask_GoodsPicked_11" messageRef="message_dbcbb1d7-70dd-4277-bad2-a8686b6454c5" name="GoodsPicked">' 
 + '      <bpmn:incoming>sequenceFlow_18d8a3bc-87d8-47b6-a7b9-9e67dc411f22</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_a9cad96d-3596-4a00-9ee3-e145a0a5ba0c</bpmn:outgoing>' 
 + '    </bpmn:receiveTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_18d8a3bc-87d8-47b6-a7b9-9e67dc411f22" sourceRef="SendTask_PickGoods_10" targetRef="ReceiveTask_GoodsPicked_11"/>' 
 + '    <bpmn:sendTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter" id="SendTask_ShipGoods_12" name="ShipGoods">' 
 + '      <bpmn:extensionElements>' 
 + '        <camunda:field name="type">' 
 + '          <camunda:string>Command</camunda:string>' 
 + '        </camunda:field>' 
 + '        <camunda:field name="name">' 
 + '          <camunda:string>ShipGoods</camunda:string>' 
 + '        </camunda:field>' 
 + '      </bpmn:extensionElements>' 
 + '      <bpmn:incoming>sequenceFlow_a9cad96d-3596-4a00-9ee3-e145a0a5ba0c</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_6260862d-3a1c-4462-b02d-bedd8a6a3933</bpmn:outgoing>' 
 + '    </bpmn:sendTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_a9cad96d-3596-4a00-9ee3-e145a0a5ba0c" sourceRef="ReceiveTask_GoodsPicked_11" targetRef="SendTask_ShipGoods_12"/>' 
 + '    <bpmn:receiveTask id="ReceiveTask_ShipmentShipped_13" messageRef="message_7de97fd2-cb9d-4ca5-a288-35fc5d2535d3" name="ShipmentShipped">' 
 + '      <bpmn:incoming>sequenceFlow_6260862d-3a1c-4462-b02d-bedd8a6a3933</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_908e7763-8efe-4e82-8a27-6906faddfdcb</bpmn:outgoing>' 
 + '    </bpmn:receiveTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_6260862d-3a1c-4462-b02d-bedd8a6a3933" sourceRef="SendTask_ShipGoods_12" targetRef="ReceiveTask_ShipmentShipped_13"/>' 
 + '    <bpmn:sendTask camunda:class="io.flowing.retail.commands.orchestration.dsl.camunda.IssueEventAdapter" id="SendTask_OrderCompleted_14" name="OrderCompleted">' 
 + '      <bpmn:extensionElements>' 
 + '        <camunda:field name="type">' 
 + '          <camunda:string>Event</camunda:string>' 
 + '        </camunda:field>' 
 + '        <camunda:field name="name">' 
 + '          <camunda:string>OrderCompleted</camunda:string>' 
 + '        </camunda:field>' 
 + '      </bpmn:extensionElements>' 
 + '      <bpmn:incoming>sequenceFlow_908e7763-8efe-4e82-8a27-6906faddfdcb</bpmn:incoming>' 
 + '      <bpmn:outgoing>sequenceFlow_187b2b7a-8dc7-4f3e-9e4a-6b1b03c542be</bpmn:outgoing>' 
 + '    </bpmn:sendTask>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_908e7763-8efe-4e82-8a27-6906faddfdcb" sourceRef="ReceiveTask_ShipmentShipped_13" targetRef="SendTask_OrderCompleted_14"/>' 
 + '    <bpmn:endEvent id="endEvent_d14e35cb-a8ee-48c5-8694-0343d0aca0fc">' 
 + '      <bpmn:incoming>sequenceFlow_187b2b7a-8dc7-4f3e-9e4a-6b1b03c542be</bpmn:incoming>' 
 + '    </bpmn:endEvent>' 
 + '    <bpmn:sequenceFlow id="sequenceFlow_187b2b7a-8dc7-4f3e-9e4a-6b1b03c542be" sourceRef="SendTask_OrderCompleted_14" targetRef="endEvent_d14e35cb-a8ee-48c5-8694-0343d0aca0fc"/>' 
 + '  </bpmn:process>' 
 + '  <bpmn:message id="message_1023f062-44c8-4388-86c6-811c31e1b0eb" name="GoodsReserved"/>' 
 + '  <bpmn:message id="message_de5acff0-f2fb-4720-9ef1-e8eb5ea22795" name="PaymentReceived"/>' 
 + '  <bpmn:message id="message_dbcbb1d7-70dd-4277-bad2-a8686b6454c5" name="GoodsPicked"/>' 
 + '  <bpmn:message id="message_7de97fd2-cb9d-4ca5-a288-35fc5d2535d3" name="ShipmentShipped"/>' 
 + '</bpmn:definitions>';


var autoLayout = new AutoLayout();

autoLayout.layoutProcess(xmlWithoutDi, function(xml) {
    console.log(xml);
});
