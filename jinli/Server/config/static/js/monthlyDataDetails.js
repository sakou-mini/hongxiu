layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});


layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});

function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: '每月数据详情'
            ,height:500
            ,limit:12
            ,loading:false
            , cols: [[
                {field: 'm', title: '月份',align:"center"},
                {field: 'd1', title: '新增总用户',align:"center"},
                {field: 'd2', title: '消费总用户',align:"center"},
                {field: 'd3', title: '访问IP',align:"center"},
                {field: 'd4', title: '消费总额',align:"center"},
            ]],
            id: 'testReload'
        });
    });
}


$("#back").on("click",function () {
    window.history.go(-1)
})

$.get({
    url:window.ioApi.user.monthUserDataDetail,
    success:function(res){
        var list = []
        for(var i = 0;i<res.monthData.length;i++){
            var data = {}
            data.m = parseInt(dateFormat("mm",res.monthData[i].statisticTime))+"月"
            data.d1=res.monthData[i].statisticItems.REGISTER_NUM
            data.d2=res.monthData[i].statisticItems.SPEND_NUM
            data.d3=res.monthData[i].statisticItems.IP_NUM
            data.d4=res.monthData[i].statisticItems.SPEND_AMOUNT
            list.push(data)
        }
        tableData(list)
    }
})

$.get({
    url:window.ioApi.user.currentMonthDataSummary,
    success:function(res){
        $("#m-IP_NUM").text(res.statisticItems.IP_NUM);
        $("#m-REGISTER_NUM").text(res.statisticItems.REGISTER_NUM);
        $("#m-SPEND_AMOUNT").text(res.statisticItems.SPEND_AMOUNT);
        $("#m-SPEND_NUM").text(res.statisticItems.SPEND_NUM)
    }
})
$.get({
    url:window.ioApi.user.todayUserDataSummary,
    success:function(res){
        $("#d-IP_NUM").text(res.statisticItems.IP_NUM);
        $("#d-REGISTER_NUM").text(res.statisticItems.REGISTER_NUM);
        $("#d-SPEND_AMOUNT").text(res.statisticItems.SPEND_AMOUNT);
        $("#d-SPEND_NUM").text(res.statisticItems.SPEND_NUM)
    }
})