webpackJsonp([22],{"4JJj":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=a("mvHQ"),l=a.n(i),o=a("LmKy"),s=a("T60X"),n=a("IcnI"),r=document.location.hostname,c={name:"imageMake",components:{},data:function(){return{listLoading:!1,centerSelect:[],moduleSelect:[],envSelect:[],moduleObject:{moduleType:0},svnCompileFileList:[],dockerFileList:[],tableData:[],queryParams:{centerId:"",moduleType:"",userId:"",beginTime:"",endTime:"",moduleName:"",pageNum:1,pageSize:10},rangeTime:"",pages:1,total:1,dialogFormVisible:!1,formData:{centerId:"",moduleId:"",envId:"",compileType:0,compileCommand:"mvn -T 4 clean package -Dmaven.test.skip=true --settings /data/maven/settings.xml",compilerFile:"",dockerfileType:1,dockerfile:"",dockerfilePath:""},editDialogFormVisible:!1,editFormData:{compileCommand:"mvn -T 4 clean package -Dmaven.test.skip=true --settings /data/maven/settings.xml"},dialogData:{data:{},type:"add"},rules:{compileType:[{required:!0}]},fileTree:[],defaultProps:{children:"children",label:"name",disabled:"dir"},selectTreeNodeData:{},webSocket:null,taskSemaphore:0,taskJobTmp:[],envPermission:{},autoFindExpanded:[],autoFindChecked:[],compileEnvSelect:[{name:"默认",value:""},{name:"开发环境dev",value:"dev"},{name:"环境sit",value:"sit"},{name:"测试环境pst",value:"pst"},{name:"比对环境bd",value:"bd"},{name:"环境uat",value:"uat"},{name:"灾备环境gray",value:"gray"},{name:"生产环境prod",value:"prod"},{name:"压测环境pt",value:"pt"}],compileConfigSelect:[{name:"默认",value:"settings"},{name:"客服中心",value:"settings2"},{name:"前端",value:"date"}],compileConfigPrams:"settings",compileEnvPrams:"",detailsDialog:!1}},filters:{getModuleTypeName:function(e){return 0==e?"svn源码类型":1==e?"程序包类型":2==e?"git源码类型":3==e?"svn自动更新":""}},methods:{handleCommand:function(e){"compileDefaultA"==e&&((1==this.editDialogFormVisible?this.editFormData:this.formData).compileCommand="mvn -T 4 clean package -Dmaven.test.skip=true --settings /data/maven/settings.xml",this.compileEnvPrams="",this.compileConfigPrams="settings")},compileEnvSelectChange:function(){var e=1==this.editDialogFormVisible?this.editFormData:this.formData;"date"==this.compileConfigPrams&&(this.compileConfigPrams="settings");var t=this.compileConfigPrams,a=this.compileEnvPrams?" -P"+this.compileEnvPrams:"";"sit"==this.compileEnvPrams||"pst"==this.compileEnvPrams||"pt"==this.compileEnvPrams||"settings2"==this.compileConfigPrams?e.compileCommand="mvn -T 4 clean package -Dmaven.test.skip=true --settings /data1/maven/"+t+".xml"+a:e.compileCommand="mvn -T 4 clean package -Dmaven.test.skip=true --settings /data/maven/"+t+".xml"+a},compileConfigSelectChange:function(e){"date"==e?((1==this.editDialogFormVisible?this.editFormData:this.formData).compileCommand=e,this.compileEnvPrams=""):this.compileEnvSelectChange()},openModuleDialog:function(e,t){this.autoFindChecked=[],this.autoFindExpanded=[],"editTask"==e?(this.editFormData={jobId:t.jobId,moduleId:t.moduleId,envId:t.envId,compileType:0,dockerfileType:1,compileCommand:"mvn -T 4 clean package -Dmaven.test.skip=true --settings /data/maven/settings.xml"},this.dialogData={data:t,type:"edit"},this.editDialogFormVisible=!0,this.getFoldersByModuleId(t.moduleId)):(this.dialogFormVisible=!0,this.getEnvList("add"))},getOneJob:function(e){var t=this;Object(s.f)({jobId:e.jobId}).then(function(e){var a=e.data.code;t.dialogData.data=200==a?e.data.data:{}})},openDetailsDialog:function(e,t){this.dialogData.type=t,this.getOneJob(e),this.detailsDialog=!0},closeDetailsDialog:function(){this.dialogData={data:{},type:"add"}},closeForm:function(){this.dialogFormVisible=!1,this.$refs.taskForm.resetFields(),this.svnCompileFileList=[],this.dockerFileList=[],this.moduleObject={moduleType:0},this.moduleSelect=[],this.fileTree=[],this.dialogData={data:{},type:"add"},this.compileEnvPrams="",this.compileConfigPrams="settings"},editCloseForm:function(){this.editDialogFormVisible=!1,this.$refs.editTaskForm.resetFields(),this.svnCompileFileList=[],this.dockerFileList=[],this.fileTree=[],this.dialogData={data:{},type:"add"},this.compileEnvPrams="",this.compileConfigPrams="settings"},searchEnvSelectChange:function(e){""!=e&&this.getCenter(e),this.queryParams.centerId="",this.envPermission=this.$store.state.user.permissionList.filter(function(t){return t.envId==e})[0],this.getList()},envSelectChange:function(e){this.formData.centerId="",this.centerSelect=[],this.moduleObject={moduleType:0},this.moduleSelect=[],this.fileTree=[],this.getCenter(e),this.$forceUpdate()},centerSelectChange:function(e){this.moduleObject={moduleType:0},this.moduleSelect=[];var t=this;Object(o.a)({centerId:e}).then(function(e){200===e.data.code?t.moduleSelect=e.data.data:(t.moduleSelect=[],t.moduleObject={moduleType:0},t.formData.moduleId=null)}),this.$forceUpdate()},moduleSelectChange:function(e){this.formData.moduleId=e.id,this.resetUploadFile(),this.getFoldersByModuleId(this.moduleObject.id),this.$forceUpdate()},getCenter:function(e){var t=this;Object(o.b)({envId:e}).then(function(e){var a=e.data.code;t.centerSelect=200==a?e.data.data:[]})},getEnvList:function(e){var t=this,a=this;Object(o.c)().then(function(i){200==i.data.code?(a.envSelect=i.data.data,"created"==e&&(a.queryParams.envId="",0!=a.envSelect.length?t.$nextTick(function(){a.queryParams.envId=a.envSelect[0].id,this.getCenter(a.envSelect[0].id),this.envPermission=this.$store.state.user.permissionList.filter(function(e){return e.envId==a.envSelect[0].id})[0]}):a.envPermission=a.$store.state.user.permissionList.filter(function(e){return e.envId==a.queryParams.envId})[0]),t.$route.query&&"created"==e&&(a.queryParams.envId=t.$route.query.data,a.envPermission=a.$store.state.user.permissionList.filter(function(e){return e.envId==a.queryParams.envId})[0]),"add"==e&&a.queryParams.envId&&(a.formData.envId=a.queryParams.envId,a.envSelectChange(a.formData.envId))):a.envSelect=[],"add"!=e&&a.getList()})},resetUploadFile:function(){this.formData.compilerFile="",this.formData.dockerfile="",this.svnCompileFileList=[],this.dockerFileList=[]},fileUploadFileHandler:function(e,t){this.editDialogFormVisible?(0!=this.dialogData.data.moduleType&&2!=this.dialogData.data.moduleType&&3!=this.dialogData.data.moduleType||1!=this.editFormData.dockerfileType||(this.editFormData.dockerfile=e.raw,this.dockerFileList=t),1==this.dialogData.data.moduleType&&1==this.editFormData.dockerfileType&&(this.editFormData.dockerfile=e.raw,this.dockerFileList=t)):(0!=this.moduleObject.moduleType&&2!=this.moduleObject.moduleType&&3!=this.dialogData.data.moduleType||1!=this.formData.dockerfileType||(this.formData.dockerfile=e.raw,this.dockerFileList=t),1==this.moduleObject.moduleType&&1==this.formData.dockerfileType&&(this.formData.dockerfile=e.raw,this.dockerFileList=t))},moduleUploadFileHandler:function(e,t){this.editDialogFormVisible?0!=this.dialogData.data.moduleType&&2!=this.dialogData.data.moduleType&&3!=this.dialogData.data.moduleType||1!=this.editFormData.compileType||(this.editFormData.compilerFile=e.raw,this.svnCompileFileList=t):0!=this.moduleObject.moduleType&&2!=this.moduleObject.moduleType&&3!=this.dialogData.data.moduleType||1!=this.formData.compileType||(this.formData.compilerFile=e.raw,this.svnCompileFileList=t)},getFoldersByModuleId:function(e){var t=this;Object(s.b)({moduleId:e}).then(function(e){var a=e.data.code;t.fileTree=200==a?[e.data.data]:[]})},handleSizeChange:function(e){this.queryParams.pageSize=e,this.getList()},handleCurrentChange:function(e){this.queryParams.pageNum=e,this.getList()},queryTimeRangeHandler:function(){this.rangeTime?(this.queryParams.beginTime=this.rangeTime[0],this.queryParams.endTime=this.rangeTime[1]):(this.queryParams.beginTime="",this.queryParams.endTime="")},getList:function(){var e=this;e.listLoading=!0,e.queryTimeRangeHandler(),Object(s.g)(e.queryParams).then(function(t){var a=t.data.data,i=t.data.code;200===i?(e.tableData=a.list,e.total=a.total,e.pages=a.pages,e.queryParams.pageNum=a.pageNum,e.queryParams.pageSize=a.pageSize):"未查询到任何数据"!=t.data.code&&201!=i||(e.tableData=[]);setTimeout(function(){e.listLoading=!1},200)})},openDetails:function(e,t){var a=this;e.permission=t,e.envId=this.queryParams.envId,n.a.dispatch("setLastRouteData",e).then(function(){a.$router.push({name:"details"})})},deleteTask:function(e){var t=this,a=this;this.$confirm("确定要删除吗?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){Object(s.a)({jobId:e.jobId}).then(function(e){var i=e.data.code;t.$message({message:200==i?"删除成功":e.data.msg,type:200==i?"success":"error"}),200==i&&a.getList()})}).catch(function(){t.$message({type:"info",message:"已取消删除"})})},selectChange:function(e){var t=this;this.taskJobTmp=[],e.forEach(function(e){t.taskJobTmp.push(e.jobId)})},NodeClickHandler2:function(e){this.$refs.tree.setCheckedKeys([],!1),this.$refs.tree.setCheckedKeys([e.absolutePath],!0),this.selectTreeNodeData=e,(1==this.editDialogFormVisible?this.editFormData:this.formData).dockerfilePath=e.absolutePath},NodeClickHandler:function(e){e.dir||(this.$refs.tree.setCheckedNodes([e]),(1==this.editDialogFormVisible?this.editFormData:this.formData).dockerfilePath=e.absolutePath)},submitForm:function(){var e=this,t=this,a="add"==this.dialogData.type?"taskForm":"editTaskForm",i="add"==this.dialogData.type?s.h:s.i,l="add"==this.dialogData.type?t.formData:t.editFormData;this.$refs[a].validate(function(a){if(!a)return!1;var o=new FormData;for(var s in 0===l.compileType?delete l.compilerFile:delete l.compileCommand,0===l.dockerfileType?delete l.dockerfile:delete l.dockerfilePath,"edit"==e.dialogData.type&&(l.dockerfile||l.dockerfilePath||delete l.dockerfileType,l.compilerFile||l.compileCommand||delete l.compileType),l)o.append(s,l[s]);i(o).then(function(e){t.closeDialog(e)})})},closeDialog:function(e){200==e.data.code?(this.dialogFormVisible=!1,this.editDialogFormVisible=!1,this.getList()):this.$message({message:e.data.msg,type:"error"})},runJob:function(e,t,a,i){var l=this,o=this;if(1!==this.taskSemaphore){var s=function(e){l.$confirm("确定要进行构建吗?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){o.taskSemaphore=1,o.initWebSocket(a,i,e)}).catch(function(){l.$message({type:"info",message:"已取消构建"})})};if("single"===t)this.taskJobTmp=[],this.taskJobTmp.push(e.jobId);else if(0===this.taskJobTmp.length)return void this.$message({type:"warning",message:"请先勾选要构建的任务!"});this.$confirm("是否离网构建?","提示",{distinguishCancelAndClose:!0,confirmButtonText:"是(速度快)",cancelButtonText:"否(速度慢，拉取私服)",type:"info"}).then(function(){return s(1)}).catch(function(e){"cancel"===e&&s(0)})}else this.$message({type:"warning",message:"请等待当前构建任务执行完毕!"})},permissionCheck:function(e){var t=[];return void 0!=this.envPermission&&this.envPermission.permissionSet&&(t=this.envPermission.permissionSet),t.includes(e)},percentage:function(e){return Math.round(e.current/e.total*1e4)/100},refreshSchedule:function(e){var t=e.result,a=e.status;if(0===a){var i=this.tableData.filter(function(t){return t.jobId===e.jobId});0!==i.length&&this.$set(i[0],"progress",this.percentage(e))}if(1===a){var l=this.tableData.filter(function(t){return t.jobId===e.jobId});0!==l.length&&(this.$set(l[0],"taskResult",t?"successful":"failed"),this.$delete(l[0],"progress"))}},initWebSocket:function(e,t){var a=arguments.length>2&&void 0!==arguments[2]?arguments[2]:null,i=null!=a?"&isOffline="+a:"",l="ws://"+(r+":6001")+"/websocket/jenkins/runJob/?X-token="+this.$store.getters.token+"&envId="+this.queryParams.envId+"&isUpdate="+e+"&isNeedUpCode="+t+i;this.webSocket=new WebSocket(l),this.webSocket.onopen=this.webSocketOnOpen,this.webSocket.onerror=this.webSocketOnError,this.webSocket.onmessage=this.webSocketOnMessage,this.webSocket.onclose=this.webSocketClose,this.webSocketTimeOutClose(3e4)},webSocketOnOpen:function(){console.log("WebSocket连接成功");var e=l()(this.taskJobTmp);this.webSocketSend(e)},webSocketOnError:function(e){console.log("WebSocket连接发生错误"),console.log(e),this.taskSemaphore=0,this.$refs.appTable.clearSelection(),this.webSocket.close()},webSocketOnMessage:function(e){var t=JSON.parse(e.data);this.refreshSchedule(t)},webSocketSend:function(e){this.webSocket.send(e)},webSocketClose:function(){this.taskSemaphore=0,this.$refs.appTable&&this.$refs.appTable.clearSelection(),this.webSocket&&(this.webSocket.close(),this.webSocket=null,console.log("connection closed"))},webSocketTimeOutClose:function(e){var t=this;setTimeout(function(){void 0!=t.webSocket&&1!=t.webSocket.readyState&&(t.taskSemaphore=0,this.$refs.appTable&&this.$refs.appTable.clearSelection(),t.webSocket.close(),console.log("Time out "+e))},e)},fileAutoFind:function(){var e=this,t=1==this.editDialogFormVisible?this.editFormData:this.formData,a={moduleId:t.moduleId,type:1};Object(o.d)(a).then(function(a){if(200==a.data.code){var i=a.data.data;if(i){e.autoFindChecked=[i],t.dockerfilePath=i;var l=[],o=(i=i.split("/")).shift()+"/"+i.shift();i.forEach(function(e){o+=e="/"+e,l.push(o)}),e.autoFindExpanded=l}}else e.$message({type:"info",message:"未找到文件"})})}},created:function(){this.getEnvList("created")},beforeDestroy:function(){this.webSocket&&this.webSocketClose()}},d={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("el-main",[a("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[a("span",{staticClass:"row-span-class"},[e._v("环境")]),e._v(" "),a("el-select",{staticStyle:{width:"10%"},attrs:{placeholder:"请选择",size:"small"},on:{change:e.searchEnvSelectChange},model:{value:e.queryParams.envId,callback:function(t){e.$set(e.queryParams,"envId",t)},expression:"queryParams.envId"}},[a("el-option",{attrs:{label:"全部",value:""}}),e._v(" "),e._l(e.envSelect,function(e){return a("el-option",{key:e.value,attrs:{label:e.envName,value:e.id}})})],2),e._v(" "),a("span",{staticClass:"row-span-class span-other"},[e._v("子中心")]),e._v(" "),a("el-select",{staticStyle:{width:"10%"},attrs:{clearable:"",placeholder:"请选择",size:"small"},model:{value:e.queryParams.centerId,callback:function(t){e.$set(e.queryParams,"centerId",t)},expression:"queryParams.centerId"}},e._l(e.centerSelect,function(e){return a("el-option",{key:e.id,attrs:{label:e.childCenterName,value:e.id}})}),1),e._v(" "),a("span",{staticClass:"row-span-class span-other"},[e._v("类型")]),e._v(" "),a("el-select",{staticStyle:{width:"10%"},attrs:{clearable:"",placeholder:"请选择",size:"small"},model:{value:e.queryParams.moduleType,callback:function(t){e.$set(e.queryParams,"moduleType",t)},expression:"queryParams.moduleType"}},[a("el-option",{attrs:{label:"svn源码类型",value:"0"}}),e._v(" "),a("el-option",{attrs:{value:"3",label:"svn自动更新"}}),e._v(" "),a("el-option",{attrs:{label:"git源码类型",value:"2"}}),e._v(" "),a("el-option",{attrs:{label:"程序包类型",value:"1"}})],1)],1),e._v(" "),a("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[a("span",{staticClass:"row-span-class"},[e._v("更新时间")]),e._v(" "),a("el-date-picker",{attrs:{"end-placeholder":"结束日期","start-placeholder":"开始日期",type:"datetimerange"},model:{value:e.rangeTime,callback:function(t){e.rangeTime=t},expression:"rangeTime"}}),e._v(" "),a("span",{staticClass:"row-span-class span-other"},[e._v("任务")]),e._v(" "),a("el-input",{staticClass:"input-class",attrs:{clearable:"",placeholder:"请输入任务名"},model:{value:e.queryParams.jobName,callback:function(t){e.$set(e.queryParams,"jobName",t)},expression:"queryParams.jobName"}}),e._v(" "),a("span",{staticClass:"row-span-class span-other"},[e._v("模块")]),e._v(" "),a("el-input",{staticClass:"input-class",attrs:{clearable:"",placeholder:"请输入模块名"},model:{value:e.queryParams.moduleName,callback:function(t){e.$set(e.queryParams,"moduleName",t)},expression:"queryParams.moduleName"}}),e._v(" "),a("el-button",{staticClass:"span-other",attrs:{icon:"el-icon-search",type:"primary"},on:{click:e.getList}},[e._v("查询")])],1),e._v(" "),a("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[e.permissionCheck("image_make_run_job")?a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.runJob(null,"multi",0,1)}}},[e._v("批量构建\n      ")]):e._e(),e._v(" "),e.permissionCheck("image_make_run_job")?a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.runJob(null,"multi",1,1)}}},[e._v("批量更新构建\n      ")]):e._e(),e._v(" "),e.permissionCheck("image_make_run_job")?a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.runJob(null,"multi",1,0)}}},[e._v("批量增量构建\n      ")]):e._e(),e._v(" "),e.permissionCheck("image_make_create_task")?a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.openModuleDialog("creatTask",1)}}},[e._v("创建新任务\n      ")]):e._e(),e._v(" "),a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:e.getList}},[e._v("刷新")])],1),e._v(" "),a("el-card",[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.listLoading,expression:"listLoading"}],ref:"appTable",staticStyle:{width:"100%"},attrs:{data:e.tableData,"highlight-current-row":!0,"element-loading-text":"拼命加载中..."},on:{"selection-change":e.selectChange}},[a("el-table-column",{attrs:{type:"selection",width:"50"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"序号",type:"index",width:"50"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n            "+e._s(t.$index+1+e.queryParams.pageSize*(e.queryParams.pageNum-1))+"\n          ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"任务名称",prop:"jobName","show-overflow-tooltip":!0}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"中心名称",prop:"centerName"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"子中心名称",prop:"childCenterName"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"环境名称",prop:"envName"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"模块名称",prop:"moduleName"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"模块类型",prop:"moduleType"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n            "+e._s(e._f("getModuleTypeName")(t.row.moduleType))+"\n          ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"状态",prop:"status"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n            "+e._s(0==t.row.status?"暂无":"已生成")+"\n          ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"构建状态",prop:"status"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.progress?a("el-progress",{staticStyle:{"font-size":"8px"},attrs:{percentage:t.row.progress,"stroke-width":12,"text-inside":!0}}):e._e(),e._v(" "),t.row.taskResult?a("span",[e._v(e._s(t.row.taskResult))]):e._e()]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"生成记录/日志",width:"120"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("a",{staticClass:"textClick-class",on:{click:function(a){return e.openDetails(t.row,e.envPermission)}}},[e._v("详情")])]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"镜像更新时间",prop:"updateTime",width:"180"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n            "+e._s(t.row.updateTime||"暂未生成")+"\n          ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:"操作",width:"250"},scopedSlots:e._u([{key:"default",fn:function(t){return[e.permissionCheck("image_make_run_job")?a("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"primary"},on:{click:function(a){return e.runJob(t.row,"single",0,1)}}},[e._v("构建\n            ")]):e._e(),e._v(" "),e.permissionCheck("image_make_update")?a("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"success"},on:{click:function(a){return e.openDetailsDialog(t.row,"details")}}},[e._v("详情\n            ")]):e._e(),e._v(" "),e.permissionCheck("image_make_update")?a("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"primary"},on:{click:function(a){return e.openModuleDialog("editTask",t.row)}}},[e._v("编辑\n            ")]):e._e(),e._v(" "),e.permissionCheck("image_make_delete")?a("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"danger"},on:{click:function(a){return e.deleteTask(t.row)}}},[e._v("删除\n            ")]):e._e()]}}])})],1)],1),e._v(" "),a("el-dialog",{attrs:{visible:e.dialogFormVisible,title:"创建新任务",top:"5vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose},on:{"update:visible":function(t){e.dialogFormVisible=t},close:e.closeForm}},[a("el-form",{ref:"taskForm",staticStyle:{width:"100%","max-height":"85vh"},attrs:{model:e.formData,rules:e.rules,"label-position":"left","label-width":"85px"}},[a("el-row",{attrs:{gutter:20}},[a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"选择环境",prop:"envId"}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"Please select"},on:{change:e.envSelectChange},model:{value:e.formData.envId,callback:function(t){e.$set(e.formData,"envId",t)},expression:"formData.envId"}},e._l(e.envSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.envName,value:e.id}})}),1)],1)],1),e._v(" "),a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"选择子中心",prop:"centerId"}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"Please select"},on:{change:e.centerSelectChange},model:{value:e.formData.centerId,callback:function(t){e.$set(e.formData,"centerId",t)},expression:"formData.centerId"}},e._l(e.centerSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.childCenterName,value:e.id}})}),1)],1)],1)],1),e._v(" "),a("el-row",{attrs:{gutter:20}},[a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"选择模块"}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"Please select","value-key":"id"},on:{change:e.moduleSelectChange},model:{value:e.moduleObject,callback:function(t){e.moduleObject=t},expression:"moduleObject"}},e._l(e.moduleSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.moduleName,value:e}})}),1)],1)],1)],1),e._v(" "),0==e.moduleObject.moduleType||2==e.moduleObject.moduleType||3==e.moduleObject.moduleType?[a("el-row",{attrs:{gutter:20}},[a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"编译方式",prop:"compileType"}},[a("el-radio",{attrs:{label:0},model:{value:e.formData.compileType,callback:function(t){e.$set(e.formData,"compileType",t)},expression:"formData.compileType"}},[e._v("命令")]),e._v(" "),a("el-radio",{attrs:{label:1},model:{value:e.formData.compileType,callback:function(t){e.$set(e.formData,"compileType",t)},expression:"formData.compileType"}},[e._v("上传")])],1)],1),e._v(" "),a("el-col",{attrs:{span:12}},[a("el-upload",{directives:[{name:"show",rawName:"v-show",value:1==e.formData.compileType,expression:"formData.compileType == 1"}],attrs:{"auto-upload":!1,"file-list":e.svnCompileFileList,limit:1,multiple:!1,"on-change":e.moduleUploadFileHandler,action:"/"}},[a("el-button",{staticClass:"button-m-small",attrs:{icon:"el-icon-upload2",size:"mini",type:"primary"}},[e._v("\n                  上传模板\n                ")])],1)],1)],1),e._v(" "),a("el-row",{directives:[{name:"show",rawName:"v-show",value:0==e.formData.compileType,expression:"formData.compileType == 0"}],attrs:{gutter:20}},[a("el-col",{attrs:{span:6}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"环境参数","value-key":"id"},on:{change:e.compileEnvSelectChange},model:{value:e.compileEnvPrams,callback:function(t){e.compileEnvPrams=t},expression:"compileEnvPrams"}},e._l(e.compileEnvSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.name,value:e.value}})}),1)],1),e._v(" "),a("el-col",{attrs:{span:6}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"配置文件参数","value-key":"id"},on:{change:e.compileConfigSelectChange},model:{value:e.compileConfigPrams,callback:function(t){e.compileConfigPrams=t},expression:"compileConfigPrams"}},e._l(e.compileConfigSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.name,value:e.value}})}),1)],1),e._v(" "),a("el-col",{attrs:{span:12}},[a("el-dropdown",{staticStyle:{width:"100%"},attrs:{trigger:"click"},on:{command:e.handleCommand}},[a("el-input",{attrs:{autosize:{minRows:1,maxRows:4},clearable:"",placeholder:"请输入内容",type:"textarea"},model:{value:e.formData.compileCommand,callback:function(t){e.$set(e.formData,"compileCommand",t)},expression:"formData.compileCommand"}}),e._v(" "),a("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[a("el-dropdown-item",{attrs:{command:"compileDefaultA"}},[e._v("mvn -T 4 clean package -Dmaven.test.skip=true --settings\n                    /data/maven/settings.xml\n                  ")])],1)],1)],1)],1)]:e._e(),e._v(" "),a("el-row",{staticStyle:{"padding-top":"5px"},attrs:{gutter:20}},[a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"dockerfile",prop:"dockerfileType"}},[a("el-radio",{attrs:{label:0},model:{value:e.formData.dockerfileType,callback:function(t){e.$set(e.formData,"dockerfileType",t)},expression:"formData.dockerfileType"}},[e._v("文件指定")]),e._v(" "),a("el-radio",{attrs:{label:1},model:{value:e.formData.dockerfileType,callback:function(t){e.$set(e.formData,"dockerfileType",t)},expression:"formData.dockerfileType"}},[e._v("上传")])],1)],1),e._v(" "),a("el-col",{directives:[{name:"show",rawName:"v-show",value:1==e.formData.dockerfileType,expression:"formData.dockerfileType == 1"}],attrs:{span:12}},[a("el-upload",{attrs:{"auto-upload":!1,"file-list":e.dockerFileList,limit:1,multiple:!1,"on-change":e.fileUploadFileHandler,action:"/"}},[a("el-button",{staticClass:"button-m-small",attrs:{icon:"el-icon-upload2",size:"mini",type:"primary"}},[e._v("\n                上传模板\n              ")])],1)],1)],1),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:0==e.formData.dockerfileType,expression:"formData.dockerfileType == 0"}]},[a("el-button",{staticClass:"button-m-small",attrs:{icon:"el-icon-search",size:"mini",type:"primary"},on:{click:e.fileAutoFind}},[e._v("\n            自动寻找\n          ")]),e._v(" "),a("div",{staticStyle:{"max-height":"35vh","overflow-y":"scroll"}},[a("el-tree",{ref:"tree",staticClass:"filter-tree",attrs:{"check-on-click-node":!0,"check-strictly":!0,data:e.fileTree,"default-expand-all":!1,props:e.defaultProps,accordion:"","default-expanded-keys":e.autoFindExpanded,"default-checked-keys":e.autoFindChecked,"node-key":"absolutePath","show-checkbox":""},on:{check:e.NodeClickHandler},scopedSlots:e._u([{key:"default",fn:function(t){var i=t.node,l=t.data;return a("span",{staticClass:"custom-tree-node"},[a("svg-icon",{attrs:{"icon-class":1==l.dir?"dir":"file"}}),e._v(" "),a("span",[e._v(e._s(i.label))])],1)}}])})],1)],1)],2),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){return e.closeForm()}}},[e._v("关闭")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.submitForm()}}},[e._v("确定")])],1)],1),e._v(" "),a("el-dialog",{attrs:{visible:e.editDialogFormVisible,title:"编辑任务",top:"5vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose},on:{"update:visible":function(t){e.editDialogFormVisible=t},close:e.editCloseForm}},[a("el-form",{ref:"editTaskForm",staticStyle:{width:"100%","max-height":"85vh"},attrs:{model:e.editFormData,rules:e.rules,"label-position":"left","label-width":"85px"}},[0==e.dialogData.data.moduleType||2==e.dialogData.data.moduleType||3==e.dialogData.data.moduleType?[a("el-row",{attrs:{gutter:20}},[a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"编译方式",prop:"compileType"}},[a("el-radio",{attrs:{label:0},model:{value:e.editFormData.compileType,callback:function(t){e.$set(e.editFormData,"compileType",t)},expression:"editFormData.compileType"}},[e._v("命令")]),e._v(" "),a("el-radio",{attrs:{label:1},model:{value:e.editFormData.compileType,callback:function(t){e.$set(e.editFormData,"compileType",t)},expression:"editFormData.compileType"}},[e._v("上传")])],1)],1),e._v(" "),a("el-col",{attrs:{span:12}},[a("el-upload",{directives:[{name:"show",rawName:"v-show",value:1==e.editFormData.compileType,expression:"editFormData.compileType == 1"}],attrs:{"auto-upload":!1,"file-list":e.svnCompileFileList,limit:1,multiple:!1,"on-change":e.moduleUploadFileHandler,action:"/"}},[a("el-button",{staticClass:"button-m-small",attrs:{icon:"el-icon-upload2",size:"mini",type:"primary"}},[e._v("\n                  上传模板\n                ")])],1)],1)],1),e._v(" "),a("el-row",{directives:[{name:"show",rawName:"v-show",value:0==e.editFormData.compileType,expression:"editFormData.compileType == 0"}],attrs:{gutter:20}},[a("el-col",{attrs:{span:6}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"环境参数","value-key":"id"},on:{change:e.compileEnvSelectChange},model:{value:e.compileEnvPrams,callback:function(t){e.compileEnvPrams=t},expression:"compileEnvPrams"}},e._l(e.compileEnvSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.name,value:e.value}})}),1)],1),e._v(" "),a("el-col",{attrs:{span:6}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"配置文件参数","value-key":"id"},on:{change:e.compileConfigSelectChange},model:{value:e.compileConfigPrams,callback:function(t){e.compileConfigPrams=t},expression:"compileConfigPrams"}},e._l(e.compileConfigSelect,function(e,t){return a("el-option",{key:t,attrs:{label:e.name,value:e.value}})}),1)],1),e._v(" "),a("el-col",{attrs:{span:12}},[a("el-dropdown",{staticStyle:{width:"100%"},attrs:{trigger:"click"},on:{command:e.handleCommand}},[a("el-input",{attrs:{autosize:{minRows:1,maxRows:4},clearable:"",placeholder:"请输入内容",type:"textarea"},model:{value:e.editFormData.compileCommand,callback:function(t){e.$set(e.editFormData,"compileCommand",t)},expression:"editFormData.compileCommand"}}),e._v(" "),a("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[a("el-dropdown-item",{attrs:{command:"compileDefaultA"}},[e._v("mvn -T 4 clean package -Dmaven.test.skip=true --settings\n                    /data/maven/settings.xml\n                  ")])],1)],1)],1)],1)]:e._e(),e._v(" "),a("el-row",{staticStyle:{"padding-top":"5px"},attrs:{gutter:20}},[a("el-col",{attrs:{span:12}},[a("el-form-item",{attrs:{label:"dockerfile",prop:"dockerfileType"}},[a("el-radio",{attrs:{label:0},model:{value:e.editFormData.dockerfileType,callback:function(t){e.$set(e.editFormData,"dockerfileType",t)},expression:"editFormData.dockerfileType"}},[e._v("文件指定")]),e._v(" "),a("el-radio",{attrs:{label:1},model:{value:e.editFormData.dockerfileType,callback:function(t){e.$set(e.editFormData,"dockerfileType",t)},expression:"editFormData.dockerfileType"}},[e._v("上传")])],1)],1),e._v(" "),a("el-col",{directives:[{name:"show",rawName:"v-show",value:1==e.editFormData.dockerfileType,expression:"editFormData.dockerfileType == 1"}],attrs:{span:12}},[a("el-upload",{attrs:{"auto-upload":!1,"file-list":e.dockerFileList,limit:1,multiple:!1,"on-change":e.fileUploadFileHandler,action:"/"}},[a("el-button",{staticClass:"button-m-small",attrs:{icon:"el-icon-upload2",size:"mini",type:"primary"}},[e._v("\n                上传模板\n              ")])],1)],1)],1),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:0==e.editFormData.dockerfileType,expression:"editFormData.dockerfileType == 0"}]},[a("el-button",{staticClass:"button-m-small",attrs:{icon:"el-icon-search",size:"mini",type:"primary"},on:{click:e.fileAutoFind}},[e._v("\n            自动寻找\n          ")]),e._v(" "),a("div",{staticStyle:{"max-height":"35vh","overflow-y":"scroll"}},[a("el-tree",{ref:"tree",staticClass:"filter-tree",attrs:{"check-on-click-node":!0,"check-strictly":!0,data:e.fileTree,"default-expand-all":!1,props:e.defaultProps,accordion:"","default-expanded-keys":e.autoFindExpanded,"default-checked-keys":e.autoFindChecked,"node-key":"absolutePath","show-checkbox":""},on:{check:e.NodeClickHandler},scopedSlots:e._u([{key:"default",fn:function(t){var i=t.node,l=t.data;return a("span",{staticClass:"custom-tree-node"},[a("svg-icon",{attrs:{"icon-class":1==l.dir?"dir":"file"}}),e._v(" "),a("span",[e._v(e._s(i.label))])],1)}}])})],1)],1)],2),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){return e.editCloseForm()}}},[e._v("关闭")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.submitForm()}}},[e._v("确定")])],1)],1),e._v(" "),a("el-dialog",{attrs:{visible:e.detailsDialog,title:"详情",top:"5vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose},on:{"update:visible":function(t){e.detailsDialog=t},close:e.closeDetailsDialog}},[a("el-row",{staticClass:"details-row",attrs:{type:"flex",justify:"space-between",gutter:10}},[a("el-col",{attrs:{span:12}},[a("span",{staticClass:"details-span"},[e._v("环境：")]),e._v(e._s(e.dialogData.data.envName)),a("span")]),e._v(" "),a("el-col",{attrs:{span:12}},[a("span",{staticClass:"details-span"},[e._v("子中心：")]),e._v(e._s(e.dialogData.data.childCenterName))])],1),e._v(" "),a("el-row",{staticClass:"details-row",attrs:{type:"flex",justify:"space-between",gutter:10}},[a("el-col",{attrs:{span:12}},[a("span",{staticClass:"details-span"},[e._v("模块类型：")]),e._v(e._s(e._f("getModuleTypeName")(e.dialogData.data.moduleType))+"\n        ")]),e._v(" "),a("el-col",{attrs:{span:12}},[a("span",{staticClass:"details-span"},[e._v("模块：")]),e._v(e._s(e.dialogData.data.moduleName))])],1),e._v(" "),3==e.dialogData.data.moduleType?a("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[a("el-col",{attrs:{span:24}},[a("span",{staticClass:"details-span"},[e._v("svn自动更新url：")]),e._v(e._s(e.dialogData.data.svnAutoUrl)+"\n        ")])],1):e._e(),e._v(" "),a("el-row",{staticClass:"details-row",attrs:{type:"flex",justify:"space-between",gutter:10}},[a("el-col",{attrs:{span:12}},[a("span",{staticClass:"details-span"},[e._v("编译命令：")]),e._v(e._s(e.dialogData.data.compileCommand))]),e._v(" "),a("el-col",{attrs:{span:12}},[a("span",{staticClass:"details-span"},[e._v("dockerfile：")]),e._v(e._s(e.dialogData.data.dockerfilePath))])],1),e._v(" "),a("el-row",{staticClass:"details-row",attrs:{type:"flex",justify:"space-between",gutter:10}},[a("el-col",{attrs:{span:24}},[a("span",{staticClass:"details-span"},[e._v("dockerfileContent：")]),e._v(" "),a("pre",{staticStyle:{"word-wrap":"break-word","white-space":"pre-wrap","max-height":"50vh","overflow-y":"auto"}},[e._v(e._s(e.dialogData.data.dockerfileContent))])])],1)],1)],1),e._v(" "),a("el-footer",{directives:[{name:"show",rawName:"v-show",value:0!=e.tableData.length,expression:"tableData.length != 0"}]},[a("div",{staticClass:"pagination-container"},[a("el-pagination",{attrs:{"current-page":e.queryParams.pageNum,"page-count":e.pages,"page-size":e.queryParams.pageSize,"page-sizes":[5,10,15,30,50],total:e.total,background:"",layout:"total, sizes, prev, pager, next, jumper"},on:{"current-change":e.handleCurrentChange,"size-change":e.handleSizeChange}})],1)])],1)},staticRenderFns:[]};var m=a("VU/8")(c,d,!1,function(e){a("D8gQ")},"data-v-077e1488",null);t.default=m.exports},D8gQ:function(e,t){},mvHQ:function(e,t,a){e.exports={default:a("qkKv"),__esModule:!0}},qkKv:function(e,t,a){var i=a("FeBl"),l=i.JSON||(i.JSON={stringify:JSON.stringify});e.exports=function(e){return l.stringify.apply(l,arguments)}}});