layui.use('layer', function () {
  var layer = layui.layer
});
layui.use('element', function () {
  var element = layui.element;
});

layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test1'
    , type: 'datetime'
  });
  laydate.render({
    elem: '#test2'
    , type: 'datetime'

  });
})

var exportData

var searchData = getFormData();
var nowPage = 1;
var size = 10
var banUserControl = 0;
function pageGat (count, nowpage) {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: size,
      curr: nowpage,
      layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
      jump: function (obj, first) {
        size = obj.limit;
        if (!first) {
          searchData = getFormData();
          searchData.page = obj.curr;
          searchData.size = obj.limit;

          $.get({
            url: window.ioApi.user.queryUserListForTotal,
            data: searchData,
            success: function (res) {
              console.log("pageLOg");
              dataCount = res.total;
              tableData(res.content);
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '获取主播列表错误'
              });
            }
          });
        }
      }
    });
  });
}
firstInitTable();
function firstInitTable () {
  searchData.page = 1
  searchData.size = size
  $.get({
    url: window.ioApi.user.queryUserListForTotal,
    data: searchData,
    success: function (res) {
      console.log(res);
      dataCount = res.total;
      tableData(res.content);
      pageGat(res.total, nowPage);
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取用户列表错误'
      });
    }
  })
}

layui.use('layer', function () {
  var layer = layui.layer
});
// 插件加载
layui.use('element', function () {
  var element = layui.element;
});

$("#searchBtn").on("click", function () {
  searchData = getFormData();
  searchData.page = 1
  searchData.size = size
  $.get({
    url: window.ioApi.user.queryUserListForTotal,
    data: searchData,
    success: function (res) {
      dataCount = res.total;
      tableData(res.content);
      pageGat(res.total, nowPage)
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取主播列表错误'
      });
    }
  })
});
$("#resetBtn").on("click", function () {
  location.reload();
});

var tData;
function tableData (list) {
  for (var i = 0; i < list.length; i++) {
    if (list[i].avatarUrl === null) {
      list[i].head = window.config.imgUrl + "/images/avatar/default/img_avatar.png"
    } else {
      list[i].head = window.config.imgUrl + list[i].avatarUrl
    }
    list[i].loginTime = dateFormat("YYYY-mm-dd HH:MM:SS", list[i].loginTime)
    list[i].createTime = dateFormat("YYYY-mm-dd HH:MM:SS", list[i].createTime)

    list[i].avgWatchLiveTime = formatSeconds(list[i].avgWatchLiveTime)
    list[i].watchLiveTime = formatSeconds(list[i].watchLiveTime)
    list[i].platformTag = getFlatformForNum(list[i].platformTag)
      list[i].status =  list[i].statue === "ACCOUNT_BAN"?"已封禁": "正常"
  }
  exportData = list
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , limit: size
      , title: '用户列表'
      , loading: false
      , cellMinWidth: 150
      , cols: [[
            { field: 'displayName', title: '用户昵称', align: "center" },
            { field: 'userId', title: '用户ID', align: "center" },
            { field: 'platformTag', title: '平台', align: "center" },
            { field: 'status', title: '状态', align: "center", width: 180 },
            { field: 'avgWatchLiveTime', title: '平均停留时间', align: "center" },
            { field: 'watchLiveTime', title: '直播间停留时间', align: "center" },
            { field: 'sendGiftCount', title: '打赏次数', align: "center" },
            { field: 'sendGiftFlow', title: '打赏金额', align: "center" },
            { field: 'connectedLiveCount', title: '连麦次数', align: "center" },
            { field: 'bulletMessageCount', title: '弹幕互动', align: "center" },
            { field: 'loginTime', title: '最近登录', align: "center", width: 180 },

            // { field: 'watchLiveCount', title: '访问直播间次数', align: "center" },
            // { field: 'watchLiveNum', title: '访问直播间ID', align: "center" },
            // { field: 'exchangeCoinAmount', title: '兑换货币', align: "center" },

            { field: 'lastIp', title: '最近登录IP', align: "center", width: 180 },
            { title: '操作', toolbar: '#barDemo', align: "center", width: 200 }
      ]],
      id: 'testReload'
    });
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        jumpDetailsUrl("userDetailsT", "userId", data.jinliUserId)
      }
    });
  });
}





function getFormData () {
    var userId = $("#userId").val()
    var displayName = $("#displayName").val()
    var ip = $("#ip").val()
    var startTime = $("#test1").val() === "" ? "" : new Date($("#test1").val()).getTime()
    var endTime = $("#test2").val() === "" ? "" : new Date($("#test2").val()).getTime()
    var platform = 2
    var watchTime =parseInt($("#watchLiveTime").val())
    var startOfLiveWatchTime= watchTime===0?0:1000
    var status = parseInt($("#status").val())
    return {
        userId,
        displayName,
        startTime,
        endTime,
        platform,
        startOfLiveWatchTime,
        statue:status,
        ip
    }
}


$("#excel").on("click", function () {
    let upData = getFormData()
    $.get({
        url: window.ioApi.user.queryUserListForTotal,
        data: upData,
        success: function (res) {
            exportData = tableFilter(res.content)
            getExcel()
        }
    })
})

function getExcel(){
    var aoa = [
        ['登录日期', '用户昵称', "用户ID",
            "访问直播间次数", "访问直播间ID", "兑换货币",
            "平均停留时间", "直播间停留时间", "打赏次数", "打赏金额",
            "连麦次数", "弹幕互动", "注册时间", "平台"
        ],
    ];
    for (var i = 0; i < exportData.length; i++) {
        var newDataArr = [];
        newDataArr.push(exportData[i].loginTime);
        newDataArr.push(exportData[i].displayName);
        newDataArr.push(exportData[i].userId);
        newDataArr.push(exportData[i].watchLiveCount);
        newDataArr.push(exportData[i].watchLiveNum);
        newDataArr.push(exportData[i].exchangeCoinAmount);
        newDataArr.push(exportData[i].avgWatchLiveTime);
        newDataArr.push(exportData[i].watchLiveTime);
        newDataArr.push(exportData[i].sendGiftCount);
        newDataArr.push(exportData[i].sendGiftFlow);
        newDataArr.push(exportData[i].connectedLiveCount);
        newDataArr.push(exportData[i].bulletMessageCount);
        newDataArr.push(exportData[i].createTime);
        newDataArr.push(exportData[i].platformTag);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '用户列表 导出日期：' + dateFormat("YYYY-mm-dd", new Date().getTime()) + '.xlsx');
}

