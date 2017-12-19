<html>
<head>
    <link href="https://cdn.bootcss.com/iview/2.7.4/styles/iview.css" rel="stylesheet" type="text/css">
    <script src="https://cdn.bootcss.com/vue/2.5.9/vue.min.js"></script>
    <script src="https://cdn.bootcss.com/iview/2.7.4/iview.min.js"></script>
    <script src="https://cdn.bootcss.com/sockjs-client/1.1.4/sockjs.min.js"></script>
    <script src="https://cdn.bootcss.com/stomp.js/2.3.3/stomp.min.js"></script>
    <script>
        var wsContext = "${wsContext}";
    </script>
</head>
<body>
<div id="app">
    <template>
        <i-input v-model="sendMsg"></i-input>
        <i-button type="primary" @click="sendMessageClickHandler">发送</i-button>
        <i-input v-model="wsMessage" type="textarea"></i-input>
    </template>

</div>
</body>
<script src="${rc.contextPath}/static/js/websocket/main.js"></script>
</html>