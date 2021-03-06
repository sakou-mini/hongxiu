var page = 1 ;
var count;
var size =10;
var roomId;
var fl;
var liveUserId
var userId
var onOne = true;
var livePattern = ""
var lastDecodedFrame;

displayRoleElement(["CLOSELIVE"],"closeRoom")
displayRoleElement(["userIdSearch"],"SEARCHROOMUSERID")


function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            jump: function(obj, first){
                page = obj.curr;
                var data = [];
                $.get({
                    url:window.ioApi.liveMonitoring.audienceList,
                    data:{
                        roomId:GetQueryString("roomId"),
                        userId:$("#userId").val(),
                        ip:$("#userIp").val(),
                        page:page,
                        size:size
                    },
                    success:function (r) {
                        count = r.total;
                        var res = r.content;
                        for(var i = 0;i<r.content.length;i++){
                            var d = {displayName:"",id:"",avatarUrl:"",vipType:"",recharge:"",phoneNumber:"",gameCoin:"",ip:""};
                            d.id = res[i].userId;
                            d.displayName = res[i].displayName;
                            d.gameCoin = res[i].gameCoin;
                            d.vipType = vipTypeFormat(res[i].vipType);
                            d.avatarUrl = res[i].avatar;
                            d.recharge = 0;
                            d.audienceIdentity = res[i].audienceIdentity
                            d.isMute = res[i].isMute
                            data.push(d)
                        }
                        tableData(data);
                    }
                })
            }
        });
    });
}

var tData;

$("#searchBtn").on("click",function () {
    var data = [];
    $.get({
        url:window.ioApi.liveMonitoring.audienceList,
        data:{
            roomId:GetQueryString("roomId"),
            userId:$("#userId").val(),
            ip:$("#userIp").val(),
            page:1,
            size:size
        },
        success:function (r) {
            count = r.total;
            var res = r.content;
            for(var i = 0;i<r.content.length;i++){
                var d = {displayName:"",id:"",avatarUrl:"",vipType:"",recharge:"",phoneNumber:"",gameCoin:"",ip:""};
                d.id = res[i].userId;
                d.displayName = res[i].displayName;
                d.gameCoin = res[i].gameCoin;
                d.vipType = vipTypeFormat(res[i].vipType);
                d.avatarUrl = res[i].avatar;
                d.recharge = 0;
                d.audienceIdentity = res[i].audienceIdentity
                d.isMute = res[i].isMute
                data.push(d)
            }

            tableData(data);
            getPage()
        }
    })
});
function tableData(list) {
    for(var i=0;i<list.length;i++){
        console.log(list);
        if(list[i].avatarUrl === null){
            list[i].avatarUrl =window.config.imgUrl+"/images/avatar/default/img_avatar.png"
        }else {
            list[i].avatarUrl = window.config.imgUrl+list[i].avatarUrl
        }
        if(list[i].audienceIdentity === "LIVEUSER"){
            list[i].isLiveUser = true;
            list[i].isAdministrator = false
        }else  if(list[i].audienceIdentity === "ADMINISTRATOR"){
            list[i].isLiveUser = false;
            list[i].isAdministrator = true
        }else {
            list[i].isLiveUser = false;
            list[i].isAdministrator = false
        }
    }
    console.log(list);
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            id:"userTable"
            ,elem: '#demo'
            , data: list
            , title: '??????????????????'
            ,loading:false
            ,done: function(res, curr, count){
                tData = res.data;//??????????????????????????????
            }
            , cols: [[
                {type: 'checkbox'},
                {field: 'avatarUrl',width:240, title: '????????????',templet:'#imgTemplet'},
                {field: 'vipType', title: '????????????'},
                {field: 'gameCoin', title: '??????',width:120},
                {field: 'recharge', title: '????????????',width: 100},
                {title: '??????', toolbar: '#barDemo'}
            ]],
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("userDetails","userId",data.id)
            }else if(obj.event === 'b'){
                layer.open({
                    type:1,
                    area:['600px','550px'],
                    title: '????????????'
                    ,content: $("#gag"),
                    shade: 0,
                    btn: ['??????']
                    ,btn1: function(index, layero){
                        var d = {
                            roomId: roomId,
                            idsStr: data.id,
                            muteTimeType: $("#muteTimeType").val(),
                            muteReason: $('input[name="reason"]:checked').val(),
                            customReason: $("#desc").val(),
                            muteArea:$("#violationType").val()
                        }
                        console.log(d);
                        $.post({
                            url:window.ioApi.liveUser.muteChatRequest,
                            data:d,
                            success:function (res) {
                                console.log(res);
                                if(res.code===200){
                                    layer.closeAll();
                                    getPage()
                                }
                            }
                        })
                    },
                    cancel: function(layero,index){
                        layer.closeAll();
                    }

                });
            }else if(obj.event === 'c'){
                layer.open({
                    title: '??????'
                    ,content: '??????????????????',
                    btn:["??????","??????"],
                    btn1:function (index,layero) {
                        layer.closeAll();
                        $.get({
                            url:window.ioApi.liveUser.unMuteChat,
                            data:{
                                roomId: roomId,
                                userId:data.id
                            },
                            success:function (res) {
                                console.log(res);
                                if(res.code===200){
                                    layer.closeAll();
                                    getPage()
                                }
                            }
                        })
                    },
                    btn2:function () {
                        layer.closeAll();
                    }
                });
            }
        });
    });
}
var hasResetBtn = true
$("#resetBtn").on("click",function () {
    $("#userId").val("");
    $("#userIp").val("");
    inserData();
});
inserData();

function inserData() {
    var data = [];
    $.get({
        url:window.ioApi.liveMonitoring.audienceList,
        data:{
            roomId:GetQueryString("roomId"),
            userId:$("#userId").val(),
            ip:$("#userIp").val(),
            page:page,
            size:size
        },
        success:function (r) {
            count = r.total;
            var res = r.content;
            for(var i = 0;i<r.content.length;i++){
                var d = {displayName:"",id:"",avatarUrl:"",vipType:"",recharge:"",phoneNumber:"",gameCoin:"",ip:""};
                d.id = res[i].userId;
                d.displayName = res[i].displayName;
                d.gameCoin = res[i].gameCoin;
                d.vipType = vipTypeFormat(res[i].vipType);
                d.avatarUrl = res[i].avatar;
                d.recharge = 0;
                d.audienceIdentity = res[i].audienceIdentity
                d.isMute = res[i].isMute
                data.push(d)
            }
            tableData(data);
            getPage()
        }
    })

}

$("#del").on("click",function () {
    layer.open({
        type:1,
        area:['600px','550px'],
        title: '????????????'
        ,content: $("#gag"),
        shade: 0,
        btn: ['??????']
        ,btn1: function(index, layero){
            var ids = [];
            $('.laytable-cell-checkbox').each(function(i,item){
                if($(item).find('.layui-form-checked').length>0){
                    if(tData[i-1] !== undefined && tData[i-1] !== ""){
                        ids.push(tData[i-1].id)
                    }
                }
            })
            var d = {
                roomId: roomId,
                idsStr: ids.join(";"),
                muteTimeType: $("#muteTimeType").val(),
                muteReason: $('input[name="reason"]:checked').val(),
                customReason: $("#desc").val(),
                muteArea:$("#violationType").val()
            }
            console.log(d);
            $.post({
                url:window.ioApi.liveUser.muteChatRequest,
                data:d,
                success:function (res) {
                    console.log(res);
                    if(res.code===200){
                        getPage()
                        layer.closeAll();
                    }
                }
            })
        },
        cancel: function(layero,index){
            layer.closeAll();
        }
    });

})


var socket;
socket = new WebSocket(window.config.wsUrl);
socket.onopen = function (event) {
    //??????
    socket.send(JSON.stringify({messageId:1000, request:{roomId:GetQueryString("roomId")}}));
    setInterval(function () {
        socket.send(JSON.stringify({messageId:1000, request:{roomId:GetQueryString("roomId")}}));
    },1000);
    //??????
    socket.onmessage = function (event) {
        var msg = JSON.parse(event.data);
        if(msg.messageId === 1000){
            initRoomData(msg.reply)
        }else if(msg.messageId === 1003){
            inserData()
        } else if(msg.messageId === 1004){
            messageInit(msg.reply)
        }else if(msg.messageId === 1005){
            giftInit(msg.reply)
        }else if(msg.messageId === 1006){
            layer.open({
                title: '??????'
                ,content: '???????????????????????????,???????????????',
                btn:["??????","??????"],
                btn1:function (index,layero) {
                    layer.closeAll();
                    jumpUrl("liveUserRoom")
                },
                btn2:function () {
                    layer.closeAll();
                }
            });
        }else if(msg.messageId === 1007){
            getPage()
        }
    };
    //??????
    socket.onclose = function(event) {
        console.log('Client notified socket has closed',event);
    };
};
function giftInit(res) {
    var user = res.userName+"("+res.userId+")";
    var gift =getGiftMsg(res.giftId)
    var vipType =vipTypeFormat(res.vipType)
    var giftBox=$("#giftMsg");
    giftBox.get(0).innerHTML +=`
    <div class="giftMsgBox">
        <span>${dateFormat("HH:MM:SS",res.sendTime)}</span><span>${user}</span><span>${vipType}</span> <span>${gift.name}</span> <span>${gift.type}</span> <span>${res.sendNum}</span>
    </div>`;
    giftBox.scrollTop( giftBox[0].scrollHeight);
}

function messageInit(res) {
    var user = res.displayName+"("+res.userId+")";
    var vipType =vipTypeFormat(res.vipType);
    var msgBox=$("#userMsg");
    msgBox.get(0).innerHTML +=`
                    <div class="userMsgBox">
                        <div class="userMsgBoxFlex">
                            <span>${dateFormat("HH:MM:SS",res.time)}</span><span>${user}</span><span>${vipType}</span>
                        </div>
                        <div>
                            <span>${res.message}</span>
                        </div>
                    </div>`;
    msgBox.scrollTop( msgBox[0].scrollHeight);
}


function initRoomData(res) {
    userId = res.userId;
    console.log(livePattern);
    if(livePattern !== "" && livePattern !==res.livePattern){
        layer.msg("????????????????????????????????????????????????")
        getLiveMsg()
        livePattern = res.livePattern;
    }else {
        livePattern = res.livePattern;
    }
    $("#livePattern").text(livePatternFilter(res.livePattern));
    $("#activeNum").text(res.activeNum);
    $("#audienceCount").text(res.audienceCount);
    $("#audienceNum").text(res.audienceNum);
    $("#chatNum").text(res.chatNum);
    $("#displayName").text(res.displayName);
    $("#gameIncome").text(res.gameIncome);
    $("#gameIncomeToRMB").text(res.gameIncome);
    $("#gameType").text(gameTypeFormat(res.gameType));
    $("#giftIncome").text(res.giftIncome);
    $("#giftIncomeToRMB").text(res.giftIncome);
    $("#liveStartTime").text(dateFormat("YYYY-mm-dd HH:MM:SS",res.liveStartTime));
    $("#liveUserId").text(res.liveUserId);
    liveUserId = res.liveUserId;
    $("#robotNum").text(res.robotNum);
    $("#roomDisplayId").text(res.roomDisplayId);
    // $("#roomId").text(res.roomId);
    $("#roomTitle").text(res.roomTitle);
    $("#userId").text(res.userId);

    $("#headRoomTitle").text(res.roomTitle);
    $("#sharedPlatform").text(sharedPlatformFilters(res.sharedPlatform))
    roomId=res.roomId;
    fl=res.hot;
    if(res.recommend){
        var recommendEndTimeF;
        recommendEndTimeF = setInterval(function () {
            var t=res.recommendEndTime-new Date().getTime();
            $("#recommendEndTime").val(formatSeconds(t))
        },1000);
    }else {
        $("#recommendEndTime").val(0)
    }

    $("#hot").on("click",function () {
        if(fl){
            fl=false
        }else {
            fl=true
        }
        $.get({
            url:window.ioApi.liveUser.updateRoomHot,
            data:{
                hot:fl,
                roomId:roomId
            },
            success:function (res) {
                console.log(res);
                if(fl){
                    $("#hot").text("????????????")
                    $("#hot").attr("class","layui-btn layui-btn-normal")
                }else {
                    $("#hot").text("????????????")
                    $("#hot").attr("class","layui-btn layui-btn-warm")
                }
            }
        })
    })

    if(res.hot){
        $("#hot").text("????????????")
        $("#hot").attr("class","layui-btn layui-btn-normal")
    }else {
        $("#hot").text("????????????")
        $("#hot").attr("class","layui-btn layui-btn-warm")
    }
    if(onOne){
        getLiveMsg()
    }
    onOne=false
    var duration;
    duration = setInterval(function () {
        var nowTime = new Date().getTime();
        var timeing = nowTime-res.liveStartTime;
        $("#duration").text(formatSeconds(timeing))
    },1000);
}

function getLiveMsg() {
    $.get({
        url:window.ioApi.liveUser.getLiveUrl,
        data:{
            userId:userId,
            streamType:"flv"
        },
        success:function (r) {
            openLive(r)
        }
    });
}


function openLive(res) {
    var mediaMsg
    console.log(res.flv);
    if(livePattern === "LIVE_AUDIO"){
        mediaMsg = {
            type:"flv",
            hasAudio:true,
            hasVideo:false,
            isLive:true,
            cors:true,
            url:res.flv[0],
        }
    }else {
        mediaMsg = {
            type:"flv",
            hasAudio:true,
            hasVideo:true,
            isLive:true,
            cors:true,
            url:res.flv[0],
        }
    }
    var element = document.getElementsByName('videoElement')[0];
    if (typeof player !== "undefined") {
        if (player != null) {
            player.unload();
            player.detachMediaElement();
            player.destroy();
            player = null;
        }
    }
    player = flvjs.createPlayer(mediaMsg, {
        enableWorker: false,
        lazyLoadMaxDuration: 3 * 60,
        seekType: 'range',
    });
    player.attachMediaElement(element);
    player.load();
    player.play();
    player.on(flvjs.Events.ERROR,function (e) {
        console.log(e);
        if(e === "NetworkError"){
            layer.open({
                title: '??????'
                ,content: '????????????????????????????????????</br>????????????????????????</br>??????????????????</br>??????????????????',
                btn:["??????","??????"],
                btn1:function (index,layero) {
                    getLiveMsg()
                },
                btn2:function () {
                    layer.closeAll();
                },
                btn3:function () {

                }
            });

        }
    });
    player.on("statistics_info", function (res) {
        console.log(lastDecodedFrame,res.decodedFrames);
        if (lastDecodedFrame === 0) {
            // loadingOver()
        }
        if (lastDecodedFrame !== res.decodedFrames) {

        } else {
            if(lastDecodedFrame !== 0){
                getLiveMsg()
            }
        }
        lastDecodedFrame = res.decodedFrames
    });
    $("#voice").on("click",function () {
        if(element.muted){
            this.className="iconfont icon-shengyin";
            element.muted = false
        }else {
            this.className="iconfont icon-jingyin";
            element.muted = true
        }
    });
    $(".videoBox").get(0).onmouseover=function () {
        $("#voice").attr("style","bottom:10px;")
    };
    $(".videoBox").get(0).onmouseout =function () {
        $("#voice").attr("style","bottom:-40px;")
    }
}

$("#closeRoom").on("click",function () {
    layer.open({
        title: '??????'
        ,content: '???????????????????????????????????????????????????????????????????????????????????????????????????',
        btn:["??????","??????"],
        btn1:function (index,layero) {
            layer.closeAll();
            $.get({
                url:window.ioApi.liveUser.closeLive,
                data:{
                    liveUserId:liveUserId
                },
                success:function (res) {
                    console.log(res);
                }
            })
        },
        btn2:function () {
            layer.closeAll();
        }
    });

})