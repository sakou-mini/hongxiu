layui.use('form', function(){
    var form = layui.form;
    form.on('submit(formDemo)', function(data){
        layer.msg(JSON.stringify(data.field));
        return false;
    });
    form.on('select(handle)', function(data){
        handle = data.value;
        console.log(handle);
        if(handle === "0"){
            console.log("fa");
            isHandle = false
        }
        else {
            isHandle = true
        }
    });
});

layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1'
    });
    laydate.render({
        elem: '#test2'
    });
});


var sendObj = {};
var page = 1;
var size = 10;
var count = 0;

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
                if(!first){
                    console.log(obj.curr);
                    analogData();
                    getPage();
                }
            }
        });
    });
}

layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});


$("#resetBtn").on("click",function () {
    location.reload();
});

analogData();

function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: "反馈列表"
            ,loading:false
            , cols: [[
                {field: 'user', title: '会员',width:230},
                {field: 'appVersion', title: '系统版本',width:80},
                {field: 'brand', title: '手机型号',width:80},
                {field: 'msg', title: '反馈信息'},
                { title: '操作', toolbar: '#barDemo',width:80}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            console.log(data);
            if(obj.event === 'b'){
                jumpDetailsUrl("userDetails","userId",data.userId)
            }
        });
    });
}

$("#searchBtn").on("click",function () {
   page = 1;
    analogData()
});


function analogData() {
    $.get({
        url:window.ioApi.service.feedbackPageInfo,
        data:{
            startTime:new Date($("#test1").val()).getTime() || "",
            endTime:new Date($("#test2").val()).getTime() || "",
            keyWords:$("#keyWords").val(),
            page:page,
            size:size
        },
        success:function(res){
            console.log(res);
            count = res.total;
            var data = [];
            for(var i = 0;i<res.content.length;i++){
                var d = {};
                d.userId = res.content[i].userId;
                d.user = res.content[i].displayName+"("+res.content[i].userId+")";
                d.msg =  res.content[i].feedbackMessage;
                d.appVersion = res.content[i].appVersion
                d.brand = brandFormat(res.content[i].brand,res.content[i].mobileMobileModel);
                data.push(d)
            }
            console.log(data);
            tableData(data);
            getPage()
        }
    })




}