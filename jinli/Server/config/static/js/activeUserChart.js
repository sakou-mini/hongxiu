var exportData ;
var dp_exportData ;
var time1 = new Date(fun_date(-6)).getTime();
var time2 = new Date(fun_date(0)).getTime();
var chartData = {
    date:["2020-1-1","2020-1-2","2020-1-3","2020-1-4","2020-1-5","2020-1-6","2020-1-7"],
    newUserDau:[156,456,156,453,154,156,451],
    oldUserDau:[245,156,655,156,132,154,135],
    wau:[4512,4065,4212,4521,4123,4123,4512,4532],
    mau:[17151,17156,17545,15464,17211,15645,12345]
}


getInfoData();
function getInfoData() {

    $.get({
        url:window.ioApi.user.getGroupMaxActiveDayData,
        data:{
            startTime:time1,
            endTime:time2
        },
        success:function (res) {
            console.log(res);
            var dataList = [
                {time:"新用户",value:res.ACTIVE_NEW_USER},
                {time:"2~3日",value:res.ACTIVE_2_3_DAY},
                {time:"4~7日",value:res.ACTIVE_4_7_DAY},
                {time:"8~14日",value:res.ACTIVE_8_14_DAY},
                {time:"15~30日",value:res.ACTIVE_15_30_DAY},
                {time:"31~90日",value:res.ACTIVE_31_90_DAY},
                {time:"91~180日",value:res.ACTIVE_91_180_DAY},
                {time:"181~365日",value:res.ACTIVE_181_365_DAY},
                {time:"1年+",value:res.ACTIVE_YEAR}
            ]


            $.get({
                url:window.ioApi.user.getActiveUserChartData,
                data:{
                    startTime:time1,
                    endTime:time2
                },
                success:function (res) {
                    if(res.data.length === 0){
                        dataRendering();
                        open(dataList)
                        return
                        // layer.open({
                        //     title: '提示'
                        //     ,content: '这个时间段内没有数据'
                        // });
                    }
                    var data = [];
                    for(var i = 0;i<res.data.length;i++ ){
                        var dataObj = {};
                        dataObj.date = dateFormat("YYYY-mm-dd",res.data[i].date);
                        dataObj.newUserDau = res.data[i].newUserDau;
                        dataObj.oldUserDau = res.data[i].oldUserDau;
                        dataObj.wau = res.data[i].wau;
                        dataObj.mau = res.data[i].mau;
                        data.push(dataObj);
                    }
                    if(data.length<2){
                        data = timeAscendingOrder(data,"date");
                    }else {
                        data = dateDataInfo(timeAscendingOrder(data,"date"));
                    }

                    var dArr=[]
                    var nArr=[];
                    var oArr =[];
                    var wArr =[];
                    var mArr=[];
                    for(var x = 0;x<data.length;x++){
                        dArr.push(data[x].date);
                        nArr.push(data[x].newUserDau);
                        oArr.push(data[x].oldUserDau);
                        wArr.push(data[x].wau);
                        mArr.push(data[x].mau)
                    }
                    chartData.date = dArr;
                    chartData.newUserDau = nArr;
                    chartData.oldUserDau = oArr;
                    chartData.wau = wArr;
                    chartData.mau = mArr;
                    open(dataList)
                }
            })


        }
    })
}


function dateDataInfo(data) {
    var dateArr = [];
    for(var x=0;x<data.length;x++){
        dateArr.push(data[x].date)
    }
    var rt = dateMakeUp(dateArr);
    if(rt === "success"){
        return data
    }else {
        for(var i=0;i<data.length;i++){
            if(data[i].date === rt){
                data.splice(i,0,{date:getNowFormatDate(data[i].date,1,"-"),newUserDau:0,oldUserDau:0,wau:0,mau:0});
                dateDataInfo(data)
            }
        }
    }
}

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
var dp_helpControl = "hidden";
var dpChart;

function dp_download() {
    var aoa = [
        ['周期', '首付玩家数','百分比'],
    ];
    for(var i = 0;i<dp_exportData.xAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dp_exportData.xAxis.data[i]);
        newDataArr.push(dp_exportData.series[0].data[i]);
        newDataArr.push(dp_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '玩家首付周期.xlsx');
}

//定义下载内容
function download(){
    if(pageControl === "DAU"){
        var aoa = [
            ['日期', '新玩家DAU', '老玩家DAU'],
        ];
        for(var i = 0;i<exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(exportData.xAxis.data[i]);
            newDataArr.push(exportData.series[0].data[i]);
            newDataArr.push(exportData.series[1].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'DAU.xlsx');
    }
    else if(pageControl === "WAU") {
        var aoa = [
            ['日期', 'WAU' ],
        ];
        for(var i = 0;i<exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(exportData.xAxis.data[i]);
            newDataArr.push(exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'WAU.xlsx');
    }
    else if(pageControl === "MAU"){
        var aoa = [
            ['日期', 'MAU' ],
        ];
        for(var i = 0;i<exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(exportData.xAxis.data[i]);
            newDataArr.push(exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'MAU.xlsx');
    }
    else {
        var aoa = [
            ['日期', 'DAU/MAU' ],
        ];
        for(var i = 0;i<exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(exportData.xAxis.data[i]);
            newDataArr.push(exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'DAU/MAU.xlsx');
    }
}


timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click",function () {
    if(verificationTheTTime() === "ok"){
        if(isIE()){
            time1 = new Date(($("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
            time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        }else {
            time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
            time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
        }
        theTclose();
        innerTime();
        getInfoData()
    }
});

$("#theT_t").on("click",function () {
    $("#theT_openTime").val(fun_date(0));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})
$("#theT_y").on("click",function () {
    $("#theT_openTime").val(fun_date(-1));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})
$("#theT_w").on("click",function () {
    $("#theT_openTime").val(fun_date(-6));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})
$("#theT_m").on("click",function () {
    $("#theT_openTime").val(fun_date(-30));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
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
});

//时间选择器显示方法
function theTshow() {
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden")
}

//时间选择器隐藏方法
function theTclose(){
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden theT_hidden")
}

var option;
//帮助弹框控件
var helpControl = "hidden";
//页面控件
var pageControl = "DAU";
var myChart;
//图表配置
option = {
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            label:{
                formatter: null
            }
        }
    },
    legend: {
        data: []
    },
    grid: {
        left: '5%',
        right: '6%',
        bottom: '5%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: []
    },
    yAxis: {
        type: 'value',
        axisLabel: {
            formatter:null
        }
    },
    series: []
};


function open(data){
    console.log(data);
    dataRendering();
    dp_dataRendering(data);
}

function dp_dataRendering(dataList){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = dataList;
    var time = [];
    var value = [];
    var num = 0;
    console.log(simulationData);
    for(var i=0;i<simulationData.length;i++){
        time.push(simulationData[i].time);
        value.push(simulationData[i].value);
        num += simulationData[i].value
    }
    var pointArr = [];

    for(var x = 0;x<value.length;x++){
        var percentage = ((value[x]/num)*100).toFixed(2)+"%"
        var pointObj = {
            coord: [x,value[x]],
            value:percentage
        }
        simulationData[x].percent = percentage
        pointArr.push(pointObj)
    }

    o.title.text = "已玩天数";
    o.xAxis.data=time;
    o.legend.data = ["活跃玩家"];
    o.series = [{
        name: '活跃玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr
        }
    }];
    dp_exportData = JSON.parse(JSON.stringify(o));
    dp_HeavyLoad(o);


    $("#dp_help").on("click",function () {
        if(dp_helpControl === "show"){
            $("#dp_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            dp_helpControl = "hidden";
        }
        else {
            $("#dp_helpTypeModality").attr("class","helpTypeModality");
            dp_helpControl = "show";
        }
    });
    $("#dp_download").on("click",function () {
        dp_download()
    })

    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: simulationData,
            title: '活跃玩家',
            loading:false,
            cols: [[
                {field: 'time', title: '天数'},
                {field: 'value', title: '活跃玩家'},
                {field: 'percent', title: '百分比'}
            ]],
            id: 'testReload',
            page: false
        });
    })

}

//渲染图标
function dataRendering(){
    if(pageControl === "DAU"){
        var o = JSON.parse(JSON.stringify(option));
        o.title.text = "DAU";
        o.xAxis.data=chartData.date;
        o.legend.data = ["新玩家","老玩家"];
        o.series = [{
            name: '新玩家',
            type: 'line',
            data:chartData.newUserDau
        },{
            name: '老玩家',
            type: 'line',
            data:chartData.oldUserDau
        }]
        exportData = JSON.parse(JSON.stringify(o));
        heavyLoad(o)

    }else if(pageControl === "WAU") {
        var o = JSON.parse(JSON.stringify(option));
        o.title.text = "WAU";
        o.xAxis.data=chartData.date;
        o.legend.data = ["WAU"];
        o.series = [{
            name: 'WAU',
            type: 'line',
            data:chartData.wau
        }]
        exportData = JSON.parse(JSON.stringify(o));
        heavyLoad(o)
    }else if(pageControl === "MAU"){
        var o = JSON.parse(JSON.stringify(option));
        o.title.text = "MAU";
        o.xAxis.data=chartData.date;
        o.legend.data = ["MAU"];
        o.series = [{
            name: 'MAU',
            type: 'line',
            data:chartData.mau
        }];
        exportData = JSON.parse(JSON.stringify(o));
        heavyLoad(o)
    }else {
        var dm = [];
        for(var i=0;i<chartData.date.length;i++){
            var d = chartData.newUserDau[i]+chartData.oldUserDau[i];
            dm.push((d/chartData.mau[i]).toFixed(2))
        }
        var o = JSON.parse(JSON.stringify(option));
        o.title.text = "DAU/MAU";
        o.xAxis.data=chartData.date;
        o.legend.data = ["DAU/MAU"];
        o.series = [{
            name: 'DAU/MAU',
            type: 'line',
            data:dm
        }];
        exportData = JSON.parse(JSON.stringify(o));
        heavyLoad(o)
    }

    $("#help").on("click",function () {
        if(helpControl === "show"){
            $("#helpTypeModality").attr("class","helpTypeModality theT_hidden");
            helpControl = "hidden";
        }
        else {
            $("#helpTypeModality").attr("class","helpTypeModality");
            helpControl = "show";
        }
    });
    $("#download").on("click",function () {
        download()
    })

}

//定义点击标签事件
$(".titleClick").on("click",function () {
    pageControl = this.getAttribute("data-value")
    getInfoData()
})
//图表自适应
window.addEventListener("resize", function () {
    myChart.resize();
    dpChart.resize();
})

function dp_HeavyLoad(o) {
    var dpChartDom = document.getElementById('dp_chart');
    dpChartDom.remove();
    $("#dp").get(0).innerHTML += '<div id="dp_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var dpChartDom = document.getElementById('dp_chart');
    dpChart = echarts.init(dpChartDom);
    dpChart.setOption(o);
}

//图表重载
function heavyLoad(o) {
    var chartDom = document.getElementById('chart');
    chartDom.remove();
    $(".tabBox").get(0).innerHTML += '<div id="chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>'
    var nchartDom = document.getElementById('chart');
    myChart = echarts.init(nchartDom);
    myChart.setOption(o);
}