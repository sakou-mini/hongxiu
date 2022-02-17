var page = 1;
var size = 10;
var count = 0;
var id = null;
var startTime = null;
var endTime = null;
layui.use('element', function () {
  var element = layui.element;
});
layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test1'
  });
  laydate.render({
    elem: '#test2'
  });
});


function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , title: '动态未通过列表'
      , height: 500
      , loading: false
      , cols: [[
        { field: 'userId', title: '发布人ID', align: "center" },
        { field: 'displayName', title: '昵称', align: "center" },
        { field: 'type', title: '发布类型', align: "center" },
        { field: 'uploadTime', title: '发布时间', align: "center" },
        { field: 'backOfficeName', title: '操作人', align: "center" },
        { field: 'operationDate', title: '审批时间', align: "center" },
        { title: '操作', toolbar: '#barDemo', align: "center" }
      ]],
      id: 'testReload',
      page: false
    });
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        jumpDetailsUrl("personDiaryDetails", "id", data.id)
      }
    });
  })
}
$("#resetBtn").on("click", function () {
  $("#class").val('');
  $("#test1").val('');
  $("#test2").val('');
  id = null;
  startTime = null;
  endTime = null;
  getPersonList();
});
getPersonList();
function getPersonList () {
  // startTime = isIE()? new Date(( $("#test1").val()).replace(/-/g, '/')).getTime():new Date(($("#test1").val())).getTime()
  // endTime = isIE()? new Date(( $("#test2").val()).replace(/-/g, '/')).getTime():new Date(($("#test2").val())).getTime();
  $.get({
    url: window.ioApi.personDiary.getRejectPersonDiary,
    data: {
      id: id,
      startTime: startTime,
      endTime: endTime,
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(timeSort(filter(res.data)));
      count = res.count;
      page = 1;
      pageGat()
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '服务器错误'
      });
    }
  })
}
/**
 * @method filter 时间类型过滤器
 * @param {Array} data 渲染表格需要的数据
 * @return {Array} 返回yyy-mmm-ddd格式的时间和图片/视频的类型
 * */
function filter (data) {
  for (var i = 0; i < data.length; i++) {
    data[i].uploadTime = formatDate(data[i].uploadTime);
    data[i].operationDate = formatDate(data[i].operationDate);
    if (data[i].type === "IMAGE") {
      data[i].type = "图片"
    } else {
      data[i].type = "小视频"
    }
  }
  return data;
}
$("#searchBtn").on("click", function () {
  id = $("#class").val();
  page = 1;
  if ($("#test1").val() !== "" && $("#test2").val() !== "") {
    startTime = isIE() ? new Date(($("#test1").val()).replace(/-/g, '/')).getTime() : new Date(($("#test1").val())).getTime()
    endTime = isIE() ? new Date(($("#test2").val()).replace(/-/g, '/')).getTime() : new Date(($("#test2").val())).getTime();
  }
  $.get({
    url: window.ioApi.personDiary.getRejectPersonDiary,
    data: {
      id: id,
      startTime: startTime,
      endTime: endTime,
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(timeSort(filter(res.data)));
      count = res.count;
      page = 1;
      pageGat()
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '服务器错误'
      });
    }
  })

});

function timeSort (data) {
  data.sort(function (a, b) {
    return a.operationDate < b.operationDate ? 1 : -1
  });
  return data;
}

function pageGat () {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: size,
      curr: page,
      jump: function (obj, first) {
        if (!first) {
          page = obj.curr;
          console.log(obj.curr);
          id = $("#class").val();
          if ($("#test1").val() !== "" && $("#test2").val() !== "") {
            startTime = isIE() ? new Date(($("#test1").val()).replace(/-/g, '/')).getTime() : new Date(($("#test1").val())).getTime()
            endTime = isIE() ? new Date(($("#test2").val()).replace(/-/g, '/')).getTime() : new Date(($("#test2").val())).getTime();
          }
          $.get({
            url: window.ioApi.personDiary.getRejectPersonDiary,
            data: {
              id: id,
              startTime: startTime,
              endTime: endTime,
              page: obj.curr,
              size: size
            },
            success: function (res) {
              count = res.count;
              page = obj.curr;
              tableDataList(timeSort(filter(res.data)));
              pageGat(res.count, page)
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '服务器错误'
              });
            }
          })
        }
      }
    });
  });
}

