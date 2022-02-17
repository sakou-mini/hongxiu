layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test1'
  });
  laydate.render({
    elem: '#test2'
  });
});
var nowPage = 1;
layui.use('element', function () {
  var element = layui.element;
});

layui.use('layer', function () {
  var layer = layui.layer
});


function tableData (list) {
  for (var i = 0; i < list.length; i++) {
    console.log(list[i].applyLiveUserTime);
    list[i].applyLiveUserTime = dateFormat("YYYY-mm-dd HH:MM:SS", list[i].applyLiveUserTime)
  }
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , title: '主播审核未通过列表'
      , height: 500
      , loading: false
      , cols: [[
        { field: 'avatarUrl', title: '用户信息', templet: '#imgTemplet', width: 200 },
        { field: 'phoneNumber', title: '手机号' },
        { field: 'realName', title: '真实姓名' },
        { field: 'getGameCoin', title: '持有金币', width: 150, sort: true },
        { field: 'applicationsNumber', sort: true, title: "申请次数" },
        { field: "applyLiveUserTime", title: "申请时间" },
        { field: 'backOfficeName', title: "操作员" },
        { align: 'center', toolbar: '#barDemo', title: "操作" }
      ]],
      id: 'testReload'
    });
    //监听工具条
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        jumpDetailsUrl("liveUserDetails", "id", data.id)
      }
    });
  });
}
function pageGat (count, nowpage) {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: 10,
      curr: nowpage,
      jump: function (obj, first) {
        if (!first) {
          var data;
          var msg = $("#class").val();
          if (!upMsgVerification(msg)) {
            data = {
              page: obj.curr,
              limit: obj.limit,
              type: parseInt($("#violationType").val())
            }
          }
          else {
            data = {
              page: obj.curr,
              limit: obj.limit,
              type: parseInt($("#violationType").val()),
              msg: msg,

            }
          }
          data.start = upMsgVerification($("#test1").val()) ? new Date($("#test1").val()).getTime() : null
          data.end = upMsgVerification($("#test2").val()) ? new Date($("#test2").val()).getTime() : null
          $.get({
            url: window.ioApi.liveUser.getRejectLiveUser,
            data: data,
            success: function (res) {
              nowPage = res.count;
              for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].phoneNumber === "unbound") {
                  res.data[i].phoneNumber = "未绑定"
                }
              }
              tableData(res.data);
              pageGat(res.count, obj.curr)
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '服务器错误'
              });
            }
          });
        }
      }
    });
  });
}
var dataCount = 1;
firstTableData();
function firstTableData () {
  var data;
  var msg = $("#class").val();
  if (!upMsgVerification(msg)) {
    data = {
      page: 1,
      limit: 10,
      type: 0,
    }
  }
  else {
    data = {
      page: 1,
      limit: 10,
      type: 0,
      msg: msg.trim()
    }
  }
  data.start = upMsgVerification($("#test1").val()) ? new Date($("#test1").val()).getTime() : null
  data.end = upMsgVerification($("#test2").val()) ? new Date($("#test2").val()).getTime() : null
  $.get({
    url: window.ioApi.liveUser.getRejectLiveUser,
    data: data,
    success: function (res) {
      dataCount = res.count;
      console.log(res);
      if (res.data) {
        for (var i = 0; i < res.data.length; i++) {
          if (res.data[i].phoneNumber === "unbound") {
            res.data[i].phoneNumber = "未绑定"
          }
        }
      }
      tableData(res.data);
      pageGat(res.count, nowPage)
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '服务器错误'
      });
    }
  })
}
$("#searchBtn").on("click", function () {
  var start = upMsgVerification($("#test1").val()) ? new Date($("#test1").val()).getTime() : null
  var end = upMsgVerification($("#test2").val()) ? new Date($("#test2").val()).getTime() : null
  if (parseInt($("#violationType").val()) === 0 && !start && !end) {
    layer.msg("请选择需要搜索的类型")
  }
  else {

    var msg = $("#class").val();
    $.get({
      url: window.ioApi.liveUser.getRejectLiveUser,
      data: {
        page: 1,
        limit: 10,
        type: parseInt($("#violationType").val()),
        msg: msg.trim(),
        start,
        end
      },
      success: function (res) {
        dataCount = res.count;
        if (res.data) {
          for (var i = 0; i < res.data.length; i++) {
            if (res.data[i].phoneNumber === "unbound") {
              res.data[i].phoneNumber = "未绑定"
            }
          }
        }
        tableData(res.data);
        pageGat(res.count, nowPage)
      },
      error: function () {
        layer.open({
          title: '错误信息'
          , content: '获取主播列表错误'
        });
      }
    })
  }
})

$("#resetBtn").on("click", function () {
  location.reload();
})
function getLiveUser (id, callback) {
  $.get({
    url: window.ioApi.liveUser.getUserIdImage,
    data: {
      id: id
    },
    success: function (res) {
      callback(res)
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '服务器错误'
      });
    }
  })
}