var pc_exportData ;
var np_exportData ;
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


function np_download() {

    if(np_pageControl === "np7"){
        var aoa = [
            ['地区', '玩家贡献',"百分比"],
        ];
        for(var i = 0;i<np_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.yAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            newDataArr.push(np_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '7日地区玩家贡献.xlsx');
    }
    else if(np_pageControl === "np14"){
        var aoa = [
            ['地区', '玩家贡献',"百分比"],
        ];
        for(var i = 0;i<np_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.yAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            newDataArr.push(np_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '14日地区玩家贡献.xlsx');
    }
    else if(np_pageControl === "np30"){
        var aoa = [
            ['地区', '玩家贡献',"百分比"],
        ];
        for(var i = 0;i<np_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.yAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            newDataArr.push(np_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '30日地区玩家贡献.xlsx');
    }else if(np_pageControl === "np60"){
        var aoa = [
            ['地区', '玩家贡献',"百分比"],
        ];
        for(var i = 0;i<np_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.yAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            newDataArr.push(np_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '60日地区玩家贡献.xlsx');
    }else {
        var aoa = [
            ['地区', '玩家贡献',"百分比"],
        ];
        for(var i = 0;i<np_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.yAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            newDataArr.push(np_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '90日地区玩家贡献.xlsx');
    }
}

function pc_download(){
    if(pc_pageControl === "pc7"){
        var aoa = [
            ['日期', '7日玩家贡献率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        console.log(aoa);
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '7日玩家贡献.xlsx');
    }
    else if(pc_pageControl === "pc14"){
        var aoa = [
            ['日期', '14日玩家贡献率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '14日玩家贡献.xlsx');
    }
    else if(pc_pageControl === "pc30"){
        var aoa = [
            ['日期', '30日玩家贡献率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '30日玩家贡献.xlsx');
    }
    else if(pc_pageControl === "pc60"){
        var aoa = [
            ['日期', '60日玩家贡献率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '60日玩家贡献.xlsx');
    }
    else if(pc_pageControl === "pc90"){
        var aoa = [
            ['日期', '90日玩家贡献率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '90日玩家贡献.xlsx');
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

var option;
//帮助弹框控件
var pc_helpControl = "hidden";
var np_helpControl = "hidden";
//页面控件
var pc_pageControl = "pc7";
var np_pageControl = "np7";

var pcChart;
var npChart;

//图表配置
option = {
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis',

        //百分比配置
        axisPointer: {
            label:{
                formatter: null
            }
        }
    },
    legend: {
        data: [
            // '新玩家', '老玩家'
        ]
    },
    grid: {
        left: '5%',
        right: '6%',
        bottom: '5%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: [
            // '2020-3-4', '2020-3-5', '2020-3-6', '2020-3-7', '2020-3-8', '2020-3-9', '2020-3-10'
        ]
    },

    yAxis: {
        type: 'value',
        //y坐标百分比配置
        axisLabel: {
            formatter:null
        }
    },
    series: [
        // {
        //     name: '老玩家',
        //     type: 'line',
        //     data: [312, 250, 351, 334, 299, 355, 381]
        // },
        // {
        //     name: '新玩家',
        //     type: 'line',
        //     data: [220, 182, 191, 234, 290, 330, 310]
        // }
    ]
};

function dataRendering(){
    pc_dataRendering();
    np_dataRendering();
};

dataRendering();
//渲染图标
function pc_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = [
        {time:"2021-3-2",value:parseFloat(randomNumPlus(0.5,2,2))},
        {time:"2021-3-3",value:parseFloat(randomNumPlus(0.5,2,2))},
        {time:"2021-3-4",value:parseFloat(randomNumPlus(0.5,2,2))},
        {time:"2021-3-5",value:parseFloat(randomNumPlus(0.5,2,2))},
        {time:"2021-3-6",value:parseFloat(randomNumPlus(0.5,2,2))},
        {time:"2021-3-7",value:parseFloat(randomNumPlus(0.5,2,2))},
        {time:"2021-3-8",value:parseFloat(randomNumPlus(0.5,2,2))},
    ];
    if(pc_pageControl === "pc7"){
        //    请求事件
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value).toFixed(2));
        }
        o.tooltip.formatter =  '玩家贡献：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "7日玩家贡献";
        o.xAxis.data=time;
        o.legend.data = ["玩家贡献",];
        o.series = [{
            name: '玩家贡献',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl === "pc14") {
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value).toFixed(2));
        }
        o.tooltip.formatter =  '玩家贡献：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "14日玩家贡献";
        o.xAxis.data=time;
        o.legend.data = ["玩家贡献",];
        o.series = [{
            name: '玩家贡献',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }else if(pc_pageControl === "pc30"){
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value).toFixed(2));
        }
        o.tooltip.formatter =  '玩家贡献：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "30日玩家贡献";
        o.xAxis.data=time;
        o.legend.data = ["玩家贡献",];
        o.series = [{
            name: '玩家贡献',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }else if(pc_pageControl === "pc60"){
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value).toFixed(2));
        }
        o.tooltip.formatter =  '玩家贡献：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "60日玩家贡献";
        o.xAxis.data=time;
        o.legend.data = ["玩家贡献",];
        o.series = [{
            name: '玩家贡献',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }else if(pc_pageControl === "pc90"){
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value).toFixed(2));
        }
        o.tooltip.formatter =  '玩家贡献：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "90日玩家贡献";
        o.xAxis.data=time;
        o.legend.data = ["玩家贡献",];
        o.series = [{
            name: '玩家贡献',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }
    $("#pc_help").on("click",function () {
        if(pc_helpControl === "show"){
            $("#pc_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            pc_helpControl = "hidden";
        }
        else {
            $("#pc_helpTypeModality").attr("class","helpTypeModality");
            pc_helpControl = "show";
        }
    });
    $("#pc_download").on("click",function () {
        pc_download()
    })

}
function np_dataRendering(){
    var simulationData = [
        {time:"美国",value:randomNumPlus(0.5,2,2)},
        {time:"印度",value:randomNumPlus(0.5,2,2)},
        {time:"日本",value:randomNumPlus(0.5,2,2)},
        {time:"韩国",value:randomNumPlus(0.5,2,2)},
        {time:"欧洲",value:randomNumPlus(0.5,2,2)},
        {time:"俄罗斯",value:randomNumPlus(0.5,2,2)},
        {time:"中国",value:randomNumPlus(0.5,2,2)},
    ]
    if(np_pageControl === "np7"){
    var o = JSON.parse(JSON.stringify(option));
    var time = [];
    var value = [];
    var num = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].time);
        value.unshift(simulationData[i].value);
        num += parseFloat(simulationData[i].value)
    }
        var pointArr = [];
        for(var x = 0;x<value.length;x++){
        var percentage = ((value[x]/num)*100).toFixed(2)+"%"
        var pointObj = {
            coord: [value[x],time[x]],
            value:percentage
        }
        pointArr.push(pointObj)
    }
        o.title.text = "各地区玩家价值";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["收入金额"];
    o.series = [{
        name: '收入金额',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
            symbol:'pin',
            symbolRotate:-90
        }
    }];
    np_exportData = JSON.parse(JSON.stringify(o));
    np_HeavyLoad(o)
    }else if(np_pageControl === "np14") {
        var o = JSON.parse(JSON.stringify(option));
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += parseFloat(simulationData[i].value)
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }
        o.title.text = "各地区玩家价值";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["收入金额"];
        o.series = [{
            name: '收入金额',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else if(np_pageControl === "np30"){
        var o = JSON.parse(JSON.stringify(option));
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += parseFloat(simulationData[i].value)
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }
        o.title.text = "各地区玩家价值";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["收入金额"];
        o.series = [{
            name: '收入金额',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else if(np_pageControl === "np60") {
        var o = JSON.parse(JSON.stringify(option));
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += parseFloat(simulationData[i].value)
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "各地区玩家价值";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["收入金额"];
        o.series = [{
            name: '收入金额',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else {
        var o = JSON.parse(JSON.stringify(option));
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += parseFloat(simulationData[i].value)
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }
        o.title.text = "各地区玩家价值";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["收入金额"];
        o.series = [{
            name: '收入金额',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }

    $("#np_help").on("click",function () {
        if(np_helpControl === "show"){
            $("#np_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            np_helpControl = "hidden";
        }
        else {
            $("#np_helpTypeModality").attr("class","helpTypeModality");
            np_helpControl = "show";
        }
    });
    $("#np_download").on("click",function () {
        np_download()
    })
}


//定义点击标签事件
$(".pc_titleClick").on("click",function () {
    pc_pageControl = this.getAttribute("data-value");
    pc_dataRendering()
});

$(".np_titleClick").on("click",function () {
    np_pageControl = this.getAttribute("data-value");
    np_dataRendering()
});
//图表自适应
window.addEventListener("resize", function () {
    pcChart.resize();
    npChart.resize();
});


//图表重载
function pc_HeavyLoad(o) {
    var pcChartDom = document.getElementById('pc_chart');
    pcChartDom.remove();
    $("#pc").get(0).innerHTML += '<div id="pc_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var pcChartDom = document.getElementById('pc_chart');
    pcChart = echarts.init(pcChartDom);
    pcChart.setOption(o);
}
function np_HeavyLoad(o) {
    var npChartDom = document.getElementById('np_chart');
    npChartDom.remove();
    $("#np").get(0).innerHTML += '<div id="np_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var npChartDom = document.getElementById('np_chart');
    npChart = echarts.init(npChartDom);
    npChart.setOption(o);
}