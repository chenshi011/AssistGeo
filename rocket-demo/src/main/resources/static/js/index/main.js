window.onload = function (ev) {
    Vue.use(iview);

    var app = new Vue({
        el: '#app',
        data : {
            wsOptions : {
                url : wsContext
            },
            wsMessage : '',
            sendMsg : '{"name":"someone"}'
        },
        mounted : function () {

        },
        methods : {
            wsOpenHandler : function (event) {
                this.$Notice.open({
                    title: 'web socket',
                    desc: 'web socket open'
                });
            },
            wsMessageHandler : function (event) {
                this.wsMessage = JSON.stringify(event);
            },
            sendMessageClickHandler : function () {
                this.stompClient.send('/app/test/welcome', {}, this.sendMsg);
            },
            welcomeTopic : function (response) {
                this.wsMessage = response.body;
                console.log(response);
            }
        },
        created : function () {
            this.stompClient = new StompClient({
                endpoint : '/assist-geo/assist'
            });

            this.stompClient.addSubscribe('/topic/web/welcome', this.welcomeTopic);

            this.stompClient.connect();
        }

    });

    window.app = app;
};

var StompClient = function (options) {
    this.client = null;
    this.options = options;
    this._endpoint = options.endpoint;

    this.topics = [];
};
StompClient.prototype = {
    connect : function () {
        this.socket = new SockJS(this._endpoint)
        this.client = Stomp.over(this.socket);

        var self = this;
        this.client.connect({}, function (frame) {
            for (var i=0; i< self.topics.length;i++) {
                var topic = self.topics[i]['topic'];
                var callback = self.topics[i]['callback'];

                self.client.subscribe(topic, function (response) {
                    callback(response);
                })
            }

        })
    },
    disconnect : function () {
        this.client.disconnect();
    },
    send : function (destination, headers, body) {
        this.client.send(destination, headers, body);
    },
    addSubscribe: function (topic, callback) {
        this.topics.push({
            topic : topic,
            callback : callback
        })
    }
};