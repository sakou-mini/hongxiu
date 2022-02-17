平台      redis    mongodb
红袖       1       hongxiu-mongodb
多彩       2       duocai-mongodb

模块说明：
blog_module         红袖博客模块
common_module       公共模块
gate_module         网关模块（所有请求的入口）
live_module         红袖直播模块
model_module        实体模块
netty_module        Socket模块
protocol_module     proto模块
queue_module        公共队列模块（延时定时任务 ，使用时间轮）
web_module          红袖web模块



每当有新平台：则需要 对应的live_module 、web_module、blog_module（可选，如果需要个人动态功能），
如果无定制化需求，可以修改配置文件，无需新模块。

不同平台除了队列服务器数据库共用，其余的数据库都应当不同以达到解耦。