var imgUrl = [];

function intiLiveUpLoad (elm, callback) {
  console.log(elm);
  var el = document.getElementById(elm);
  console.log(el);
  el.innerHTML += '<label type="button" class="xdg_LoadImgBtn" id="' + elm + 'Btn">' +
    '<span>选择图片</span>' +
    '<input type="file">' +
    '</label>';
  document.getElementById(elm).addEventListener('change', function (e) {
    var files = e.target.files;
    console.log(files);
    if (!files.length) return;
    var formData = new FormData();
    formData.append("handlerType", 4);
    formData.append('file', files[0]);

    $.post({
      url: window.config.imgUrl + "/uploadImage",
      data: formData,
      cache: false,
      xhr: function () {
        return $.ajaxSettings.xhr();
      },
      contentType: false,
      processData: false,
      success: function (res) {
        console.log(res);
        callback(res)
      },
      error: function () {
        layer.open({
          title: '错误信息'
          , content: '图片上传出错'
        });
      }
    })
  });
}