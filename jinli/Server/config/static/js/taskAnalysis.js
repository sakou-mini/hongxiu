var time1 = fun_date(-6);
var time2 = fun_date(0);
// 时间选择器初始化
function timeUpdate(){
    layui.use('laydate', function(){
        var laydate = layui.laydate;
        laydate.render({
            elem: '#theT_openTime',
            value:fun_date(-6),
            max:0
        });
        laydate.render({
            elem: '#theT_endTime',
            value:fun_date(0),
            max:0
        });
    });
}


timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click",function () {
    if(verificationTheTTime() === "ok"){
        theTclose();
        innerTime();
        dataRendering();
    }
})

// 刷新时间选择器显示时间
function innerTime(){
    var day1 = formatSpecificDate(new Date($("#theT_openTime").val()));
    var day2 = formatSpecificDate(new Date($("#theT_endTime").val()));
    $("#theT_open").val(day1+" ~ "+day2);
}

//初始化时间选择器时间
$("#theT_open").val(fun_date(-6)+" ~ "+fun_date(0));

//时间控制器取消按钮
$("#theT_cancel").on("click",function () {
    theTclose();
    timeUpdate()
});

//时间选择器显示方法
function theTshow() {
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden")
}

//时间选择器隐藏方法
function theTclose(){
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden theT_hidden")
}

var pageControl = "all";


var sendObj = {};
var page = 1;
var size = 10;
var count = 0;

function tableDataList(list) {
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: list,
            title: '竞猜列表',
            height:500,
            loading:false,
            cols: [[
                {field: 'id', title: '编号'},
                {field: 'name', title: '别名'},
                {field: 'type', title: '类型'},
                {field: 'completion', title: '完成次数'},
                {field: 'receive', title: '领取次数'},
                {field: 'collectionRate', title: '领取率'}
            ]],
            id: 'testReload',
            page: false
        });
    })
}
getPage();

function getPage() {
    layui.use('laypage', function() {
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count, //数据总数，从服务端得到
            limit: size,
            curr: page,
            jump: function (obj, first) {
                if(!first){
                    analogData();
                    page = obj.curr;
                    getPage();
                }
            }
        });
    })
}

$(".titleClick").on("click",function () {
    pageControl = this.getAttribute("data-value")
    analogData()
})

analogData()

function analogData() {
    count = 100;
    var data = []
    for(var i = 0;i<10;i++){
        var d = {id:"",name:"",type:"",completion:"",receive:"", collectionRate:"" }
        var id = "";
        for(var x =0;x<4;x++){
            id+= randomNum(1,9)+""
        }
        d.id = id;
        var n
        if(pageControl === "all"){n = randomNum(1,3)}
        else if(pageControl === "newbie"){n=1;count = 30;}
        else if(pageControl === "signIn"){n=2;count = 40;}
        else if(pageControl === "daily"){n=3;count = 30;}
        if(n===1){
            d.name = "新手任务"+randomNum(1,50);
            d.type="新手"
        }else if(n===2){
            d.name = "签到任务"+randomNum(1,50);
            d.type="签到"
        }else{
            d.name = "每日任务"+randomNum(1,50);
            d.type="每日"
        }
        d.completion=randomNum(600,5000);
        d.receive = randomNum(500,d.completion);
        d.collectionRate = (d.receive/d.completion*100).toFixed(2)+"%";
        data.push(d)
    }
    tableDataList(data)
    getPage()
}
