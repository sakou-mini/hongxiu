var sendObj = {};

var form
var selectData = ""
var allLiveData = []
var selectDomains =[]
layui.use('form', function(){
    form = layui.form;
    form.render();

});

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
            , limit: 1000
            , cols: [[
                {field: 'name', title: '节点名称'},
                {field: 'line', title: '节点编号'},
                {field: 'domain', title: '域名'},
                {title: '操作', toolbar: '#barDemo'}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'b'){
                layer.open({
                    title: '提示'
                    ,content: '是否删除这个域名？',
                    btn:["确定","取消"],
                    btn1:function (index,layero) {
                        console.log(data);
                        for(let i=0;i<selectDomains.length;i++){
                            if(selectDomains[i] === data.domain){
                                selectDomains.splice(i,1)
                            }
                        }
                        console.log(selectDomains.join());
                        $.post({
                            url:window.ioApi.backOffice.updateLiveDomain,
                            data:{
                                line:data.line,
                                liveDomains:selectDomains.join()
                            },
                            success:function (res) {
                                console.log(res.code);
                                if(res.code === 200){
                                    layer.open({
                                        title: '提示'
                                        , content: '删除成功',
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


    });
}

$("#searchBtn").on("click",function () {
    selectData = $("#line").val()
    console.log(selectData);
    for(let i=0;i<allLiveData.length;i++){
        if(allLiveData[i].line === selectData){
            console.log(allLiveData[i]);
            tableData(tableInit(allLiveData[i]));
        }
    }
})

function analogData() {
    $.post({
        url: window.ioApi.backOffice.liveDomainList,
        success:function (res) {
            console.log(res);
            var lineList = res
            $("#line").get(0).innerHTML=""
            for(let i=0;i<lineList.length;i++){
                console.log("循环");
                var value = liveSourceLineFilter(lineList[i].line).type
                var name = liveSourceLineFilter(lineList[i].line).name
                $("#line").get(0).innerHTML+=`<option value="${value}">${name}</option>`
            }
            selectData = res[0].line
            allLiveData = res
            layui.use('form', function(){
                var form = layui.form;//高版本建议把括号去掉，有的低版本，需要加()
                form.render('select');
                //form.render();
            });
            tableData(tableInit(res[0]));
        }
    })
}


function tableInit(obj) {
    var tData = []
    selectDomains = []
    for(let i=0;i<obj.domains.length;i++){
        var d = {}
        d.name = liveSourceLineFilter(obj.line).name
        d.line = obj.line
        d.domain = obj.domains[i]
        selectDomains.push(obj.domains[i])
        tData.push(d)
    }
    return tData
}
function liveSourceLineFilter(type) {
    for(var i =0; i < window.liveSourceLine.length;i++){
        if(window.liveSourceLine[i].type === type){
                return window.liveSourceLine[i]
        }
    }
}



$("#add").on("click",function () {
    console.log("触发0");
    layer.open({
        type:1,
        area:['500px','400px'],
        title: '新增线路域名'
        ,content: $("#gag"),
        shade: 0,
        btn: ['提交']
        ,btn1: function(index, layero){
            var obj
            for(let i=0;i<allLiveData.length;i++){
                if(allLiveData[i].line === $("#lineUp").val()){
                    obj = allLiveData[i]
                }
            }
            var domains = obj.domains.join()
            domains+=","+$("#domain").val()
            var d = {
                line: $("#lineUp").val(),
                liveDomains: domains,
            }
            console.log(d);
            $.post({
                url:window.ioApi.backOffice.updateLiveDomain,
                data:d,
                success:function (res) {
                    console.log(res);
                    if(res.code===200){
                        layer.open({
                            title: '提示'
                            , content: '添加成功',
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
        cancel: function(layero,index){
            layer.closeAll();
        }

    });
})