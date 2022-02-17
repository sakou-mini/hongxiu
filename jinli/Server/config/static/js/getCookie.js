var backOfficeUserId = getCookie("JSESSIONID");

function getCookie(c_name){
    if (document.cookie.length>0){
        var c_start = document.cookie.indexOf(c_name + "=");
        if (c_start!=-1){
            c_start=c_start + c_name.length+1
            c_end=document.cookie.indexOf(";",c_start)
            if (c_end==-1) c_end=document.cookie.length
            return unescape(document.cookie.substring(c_start,c_end))
        }
    }
    return ""
}

$.get({
    url:"/liveUser/getBackOfficeMsg",
    data:{
        backOfficeUserId:backOfficeUserId
    },
    success:function(res){
        $("#loginName").text(res.accountName);
        localStorage.setItem("loginMsg",res);
    },
    error:function () {
        layer.open({
            title: '错误信息'
            ,content: '服务器错误，请联系管理员'
        });
    }
})
var offsetWid= document.documentElement.clientWidth;
window.onresize=function(){
    watchChangeSize();
}
var only = true;
var isSm = false;
function watchChangeSize (){
    var nowOffsetWid = document.documentElement.clientWidth;
    if(nowOffsetWid < offsetWid){
        isSm = true
    }else {
        isSm = false
    }
    offsetWid = nowOffsetWid;
    if(offsetWid <= 830 && offsetWid >= 500 && only && isSm){
        only = false;
        layer.open({
            title: '警告'
            ,content: '过低的浏览器宽度会使数据展示不全',
            btn:["确认"],
            btn1: function(index, layero){
                only = true;
                layer.closeAll();
            },cancel: function(){
                only = true;
            }
        });
    }
}
