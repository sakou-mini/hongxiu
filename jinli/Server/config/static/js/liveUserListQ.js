
var sex_chartDom = document.getElementById('sex_chart');
var sex_chart = echarts.init(sex_chartDom);

//eChart

//平台分布

// option_sex && sex_chart.setOption(option_sex);
// option_platform && platform_chart.setOption(option_platform);

displayRoleElement(["SEARCHLIVEUSERIDLIVEUSERLISTQ"],"search")

function sex_HeavyLoad(o){
    var sexChartDom = document.getElementById('sex_chart');
    sexChartDom.remove();
    $("#sex").get(0).innerHTML += '<div id="sex_chart" style="width: 80%;height:160px;margin-left: auto;margin-right: auto;background-color: #fff;"></div>';
    sexChartDom = document.getElementById('sex_chart');
    sex_chart = echarts.init(sexChartDom);
    sex_chart.setOption(o);
}

getChartData();
function getChartData() {
    $.get({
        url:window.ioApi.liveUser.getLiveUserSummary,
        data:{platform:3},
        success:function (res) {
            console.log(res);
            $("#monthOnlineAvgNum").text(res.monthOnlineAvgNum+"人");
            $("#liveUserNum").text(res.liveUserNum+"人");
            sex_initData(res.gender);
        }
    })
}


function sex_initData(data) {
    if(data.FEMALE === undefined ||data.FEMALE === null || data.FEMALE === ""){
        data.FEMALE = 0
    }

    if(data.MALE === undefined ||data.MALE === null || data.MALE === ""){
        data.MALE = 0
    }
    var option_sex = {
        title: {
            text: '性别分布'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            top: '0%',
            left: 'center'
        },
        series: [
            {
                name: '性别分布',
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: '20',
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    {value: data.MALE, name: '男'},
                    {value: data.FEMALE, name: '女'},
                ]
            }
        ]
    };
    sex_HeavyLoad(option_sex)
}

var slotNum = 1;
var type = "0";
layui.use('form', function(){
    var form = layui.form;
    form.on('select(type)', function(data){
        type = data.value;
        page = 1;
        analogData()
    });
});

layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});

var sendObj = {};
var page = 1;
var size = 10;
var count;
var pCode= -1;

function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function(obj, first){
                page = obj.curr;
                size =obj.limit;
                $.get({
                    url:window.ioApi.liveUser.getLiveUserPageList,
                    data:{
                        platform:3,
                        page:page,
                        size:size,
                        condition:$("#element").val(),
                        statue:parseInt($("#status").val())
                    },
                    success:function (res) {
                        console.log(res);
                        count = res.count;
                        tableData(res.data);
                    }
                })
            }
        });
    });
}


function tableData(list) {
    for(var i=0;i<list.length;i++){
        list[i].lastLoginTime = dateFormat("YYYY-mm-dd HH:MM:SS",list[i].lastLoginTime)
        if(!list[i].avatarUrl){
            list[i].head =window.config.imgUrl+"/images/avatar/default/img_avatar.png"
        }else {
            list[i].head = window.config.imgUrl+list[i].avatarUrl
        }
        if(list[i].liveStatus === "LIVE_BAN"){
            list[i].status = "已封禁"
        }else {
            list[i].status = "正常"
        }
        for(let x = 0;x<list[i].sharedPlatform.length;x++){
            console.log(list[i].sharedPlatform[x]);
            list[i].sharedPlatform[x] = getPlatform(list[i].sharedPlatform[x])
        }
        list[i].sharedPlatform = list[i].sharedPlatform.join("、")
    }
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            ,limit:size
            , title: "反馈列表"
            ,loading:false
            , cols: [[
                {field: 'avatarUrl', title: '主播昵称',width: 200,templet:'#imgTemplet'},
                {field: 'liveUserId',title:"主播ID",align:"center"},
                {field: 'userLevel', title: '等级',align:"center"},
                {field: 'totalIncome', title: '总收入',align:"center"},
                {field: 'onlineTime', title: '月在线时长(小时)',align:"center"},
                {field: 'status', title: '状态',align:"center"},
                {field: 'lastLoginTime', title: '上次登录时间',align:"center"},
                {field: 'totalCoin', title: '持有金币', sort: true,align:"center"},
                {field: 'sharedPlatform', title: '相关平台', align:"center"},
                {title: '操作', toolbar: '#barDemo',align:"center"}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("liveUserDetailInfoQ","liveUserId",data.liveUserId)
            }
        });
        roleTableBtn(["LIVEUSERDETAILINFOQ"],"detail")
    });
}

analogData();

function analogData() {
    $.get({
        url:window.ioApi.liveUser.getLiveUserPageList,
        data:{
            platform:3,
            page:page,
            size:size,
            statue:parseInt($("#status").val())
        },
        success:function (res) {
            console.log(res);
            count = res.count;
            tableData(res.data);
            getPage()
        }
    })
    // tableData(data)
}


$("#searchBtn").on("click",function () {
    getChartData();
    $.get({
        url:window.ioApi.liveUser.getLiveUserPageList,
        data:{
            platform:3,
            page:1,
            size:10,
            condition:$("#element").val(),
            statue:parseInt($("#status").val())
        },
        success:function (res) {
            console.log(res);
            count = res.count;
            tableData(res.data);
        }
    })
});

$("#resetBtn").on("click",function () {

});