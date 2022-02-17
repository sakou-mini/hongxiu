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
var size = 10;
// 插件加载
layui.use('element', function () {
  var element = layui.element;
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
      , title: '主播审核通过列表'
      , height: 500
      , loading: false
      , cols: [[
        { field: 'avatarUrl', title: '用户信息', templet: '#imgTemplet', width: 200 },
        { field: 'liveUserId', title: '主播ID', align: "center" },
        { field: 'phoneNumber', title: '手机号', align: "center" },
        { field: 'realName', title: '真实姓名', align: "center" },
        { field: 'getGameCoin', title: '持有金币', width: 150, sort: true, align: "center" },
        { field: 'applicationsNumber', sort: true, title: "申请次数", align: "center" },
        { field: "applyLiveUserTime", title: "申请时间", align: "center" },
        { field: 'approveState', title: "审核状态", align: "center" },
        { field: 'backOfficeName', title: "操作员", align: "center" },
        { align: 'center', toolbar: '#barDemo', title: "操作" }
      ]],
      id: 'testReload'
    });
    //监听工具条
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        jumpDetailsUrl("liveUserDetails", "id", data.userId)
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
      limit: size,
      curr: nowpage,
      jump: function (obj, first) {
        if (!first) {
          var isPass = parseInt($("#violationType").val()) === 0 ? true : false;
          var userId = $("#class").val();
          var start = upMsgVerification($("#test1").val()) ? new Date($("#test1").val()).getTime() : null
          var end = upMsgVerification($("#test2").val()) ? new Date($("#test2").val()).getTime() : null
          $.get({
            url: window.ioApi.liveUser.getPassLiveUser,
            data: {
              page: obj.curr,
              limit: obj.limit,
              userId: userId.trim(),
              isPass,
              platform: 1,
              start,
              end
            },
            success: function (res) {
              nowPage = res.count;
              for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].phoneNumber === "unbound") {
                  res.data[i].phoneNumber = "未绑定"
                }
                res.data[i].approveState = res.data[i].approveState ? "已通过" : "未通过"
              }
              tableData(res.data);
              pageGat(res.count, obj.curr)
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '获取通过主播失败'
              });
            }
          });
        }
      }
    });
  });
}
firstTableData();
function firstTableData () {
  var userId = $("#class").val();
  var isPass = parseInt($("#violationType").val()) === 0 ? true : false;
  var start = upMsgVerification($("#test1").val()) ? new Date($("#test1").val()).getTime() : null
  var end = upMsgVerification($("#test2").val()) ? new Date($("#test2").val()).getTime() : null
  $.get({
    url: window.ioApi.liveUser.getPassLiveUser,
    data: {
      page: 1,
      limit: size,
      isPass,
      userId: userId.trim(),
      platform: 1,
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
          res.data[i].approveState = res.data[i].approveState ? "已通过" : "未通过"
        }
      }
      tableData(res.data);
      pageGat(res.count, nowPage)
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取通过主播失败'
      });
    }
  })
}


$("#searchBtn").on("click", function () {
  var userId = $("#class").val();
  var isPass = parseInt($("#violationType").val()) === 0 ? true : false;
  var start = upMsgVerification($("#test1").val()) ? new Date($("#test1").val()).getTime() : null
  var end = upMsgVerification($("#test2").val()) ? new Date($("#test2").val()).getTime() : null
  $.get({
    url: window.ioApi.liveUser.getPassLiveUser,
    data: {
      page: 1,
      limit: size,
      isPass,
      userId: userId.trim(),
      platform: 1,
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
          res.data[i].approveState = res.data[i].approveState ? "已通过" : "未通过"
        }
      }
      tableData(res.data);
      pageGat(res.count, nowPage)
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取通过主播失败'
      });
    }
  })
});

$("#resetBtn").on("click", function () {
  location.reload();
});
