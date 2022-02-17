var sendObj = {};
var page = 1;
var size = 10;
var count = 0;
var slotNum = 1;
var type = "0";
layui.use('form', function(){
    var form = layui.form;
    form.on('select(type)', function(data){
        if(data.value === "0"){
            type = false;
        }else {
            type = true
        }
        page = 1;
        analogData()
    });
});

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
                if(!first){
                    analogData();
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
                {field: 'name', title: '节点名称'},
                {field: 'type', title: '节点编号'},
                {field: 'state', title: '当前状态'},
                {title: '操作', toolbar: '#barDemo'}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'b'){
                layer.open({
                    title: '提示'
                    ,content: '是否关闭这个CDN？',
                    btn:["确定","取消"],
                    btn1:function (index,layero) {
                        $.post({
                            url:window.ioApi.liveUser.openOrCloseLiveSourceLine,
                            data:{
                                liveSourceLine:data.type,
                                close:true
                            },
                            success:function (res) {
                                if(res.code === 200){
                                    layer.open({
                                        title: '提示'
                                        , content: '关闭成功',
                                        btn: ["确定"],
                                        btn1:function(){
                                            layer.closeAll();
                                            location.reload()
                                        }
                                    })
                                }
                            }
                        })
                    },
                    btn2:function () {
                        layer.closeAll();
                    }
                });
            }else if(obj.event === 'c'){
                layer.open({
                    title: '提示'
                    ,content: '是否启用这个CDN？',
                    btn:["确定","取消"],
                    btn1:function (index,layero) {
                        $.post({
                            url:window.ioApi.liveUser.openOrCloseLiveSourceLine,
                            data:{
                                liveSourceLine:data.type,
                                close:false
                            },
                            success:function (res) {
                                if(res.code === 200){
                                    layer.open({
                                        title: '提示'
                                        , content: '启用成功',
                                        btn: ["确定"],
                                        btn1:function(){
                                            layer.closeAll();
                                            location.reload()
                                        }
                                    })
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
        roleTableBtn(["OPENORCLOSELIVESOURCELINE"],"b")
        roleTableBtn(["OPENORCLOSELIVESOURCELINE"],"c")

    });
}

function analogData() {
    $.post({
        url: window.ioApi.liveUser.getLiveSourceLineList,
        success:function (res) {
            console.log(res);
            tableData(dataFilter(res.data));
        }
    })
}

function dataFilter(list) {
    var cdnList =[];
    console.log(list);
    for(var i =0; i < window.liveSourceLine.length;i++){
        var d = window.liveSourceLine[i]
        d.isOpen = isOpenFilter(d.type,list)
        d.state = d.isOpen ? "启用中" : "已停用"
        cdnList.push(d)
    }
    return cdnList
}

function isOpenFilter(type,list) {
    for(var i=0;i<list.length;i++){
        if(type === list[i]){
            return true
        }
    }
    return false
}