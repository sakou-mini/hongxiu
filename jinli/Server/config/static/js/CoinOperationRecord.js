var page = 1;
var size = 10;
var count = 0;
var accountName = null;
var displayName = null;
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
/**
 * @method tableDataList 渲染表格
 * @param {Array} list 渲染表格需要的数据
 * */
function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , title: '动态审核列表'
      , height: 500
      , limit: size
      , loading: false
      , cols: [[
        { field: 'accountName', title: '操作人', align: "center" },
        { field: 'displayName', title: '被修改玩家', align: "center" },
        { field: 'originalCoin', title: '玩家原有金币', align: "center" },
        { field: 'existingCoin', title: '修改后金币', align: "center" },
        { field: 'operationCoin', title: '修改金额', align: "center" },
        { field: 'time', title: '时间', align: "center" },
      ]],
      id: 'testReload',
      page: false
    });

  })
}
$("#resetBtn").on("click", function () {
  // 重置
  location.reload()
});

getRecordList();
function getRecordList () {
  $.get({
    url: window.ioApi.user.getRecordList,
    data: {
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(filter(res.data));
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


//search
$("#searchBtn").on("click", function () {
  page = 1;
  accountName = $("#class").val().trim();
  displayName = $("#element").val().trim();
  $.get({
    url: window.ioApi.user.getRecordList,
    data: {
      accountName: accountName,
      displayName: displayName,
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(filter(res.data));
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


//page
function pageGat () {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: size,
      curr: page,
      layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
      jump: function (obj, first) {
        if (!first) {
          page = obj.curr;
          size = obj.limit;
          accountName = $("#class").val().trim();
          displayName = $("#element").val().trim();
          $.get({
            url: window.ioApi.user.getRecordList,
            data: {
              accountName: accountName,
              displayName: displayName,
              page: obj.curr,
              size: size
            },
            success: function (res) {
              count = res.count;
              page = obj.curr;
              tableDataList(filter(res.data));
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

function filter (data) {
  for (var i = 0; i < data.length; i++) {
    data[i].time = formatDate(data[i].time)
  }
  return data;
}