var v = window.parent.vue;
function cp(search) {
    var clone ={};
    for(var field in search) {
        if(typeof search[field] === 'object' && !isNaN(search[field].length)){
            if(!clone[field]) clone[field] = [];
            for(var i=0;i<search[field].length;i++){
                clone[field][i] = {...{},...search[field][i]};
            }
        } else{
            clone[field] = {...{},...search[field]};
        }
    }
    return clone;
}
function addRule (key,item) {
    if(item.data != null && item.data!=''){
        return {
            field:key,
            data:item.data,
            op:item.op
        };
    }
    else{
        return null;
    }
}
function search2Cond(search,groupOp){
    let cond ={
        rules:[],
        groupOp:groupOp
    }
    for(let field in search) {
        if(typeof search[field] === 'object' && !isNaN(search[field].length)){
            for(var i=0;i<search[field].length;i++){
                var rule  = addRule(field,search[field][i]);
                if(rule!=null) cond.rules.push(rule);
            }
        } else{
            var rule = addRule(field,search[field]);
            if(rule!=null) cond.rules.push(rule);
        }
    }
    return cond;
}


function loadTable({data,methods,mounted,urlList,urlEdit,dialogWidth,dialogHeight,urlDel,title}){
   return new Vue({
        el: '#app',
        data:{
            ...{
                //表格当前页数据
                tableData : [],
                //默认每页数据量
                pagesize : 10,
                    //当前页码
                currentPage : 1,
                    //查询的页码
                start : 1,
                //默认数据总数
                totalCount: 0,
                search: {},
                resetSearch:{},
                multipleSelection: [],
                tableHeight:window.innerHeight-100
            }, ...data
        },
        mounted: function() {
            this.resetSearch = cp(this.search);
            this.loadData(this.currentPage, this.pagesize);
            if(mounted) mounted();
        },
        methods: {
            ...{
                //从服务器读取数据
                async loadData(pageNum, pageSize) {
                    let {data} = await
                    axios.post(urlList, {
                        "pageable": {
                            'current': pageNum,
                            'size': pageSize
                        }, "cond": search2Cond(this.search, "AND")
                    });

                    if (data.code == 0) {
                        this.tableData = data.body.items;
                        this.totalCount = data.body.total;
                    }
                    else {
                        //v.$message.error(data.message);
                    }
                },
                reload(){
                    this.loadData(this.currentPage, this.pagesize);
                },
                handleReset(){
                    this.search = this.cp(this.resetSearch);
                    this.reload();
                },
                //每页显示数据量变更
                handleSizeChange(val) {
                    this.pagesize = val;
                    this.reload();
                },
                //页码变更
                handleCurrentChange(val) {
                    this.currentPage = val;
                    this.reload();
                },
                //搜索
                handleSearch() {
                    this.reload();
                },
                handleAdd() {
                    v.dialogShow(0,{title:'添加'+title,url:urlEdit,width:dialogWidth,height:dialogHeight});
                },
                handleEdit(index, row) {
                    v.dialogShow(0,{title:'修改'+title,url:urlEdit+"?id="+row.id,width:dialogWidth,height:dialogHeight});
                },
                handleDel(index, row) {
                    //window.parent
                    this.delById(row.id);
                },
                handleDelBatch() {
                    //window.parent
                    if(this.multipleSelection.length==0){
                        v.$message({type: 'error', message: '删除失败!至少选择一行！'});
                        return;
                    }
                    let ids = this.multipleSelection.map(r=>r.id).join(',');
                    this.delById(ids);
                },
                handleSelectionChange(val) {
                    this.multipleSelection = val;
                },
                async delById(ids){
                    let r = await v.$confirm('此操作将删除该' + title + ', 是否继续?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'}).catch(() => {});
                    if(r=='confirm') {
                        let {data} = await axios.delete(urlDel + ids);
                        if (data.code == 0) {
                            this.$message({type: 'success', message: '删除成功!'});
                            this.reload();
                        }
                        else {
                            this.$message({type: 'error', message: data.message});

                        }
                    }
                }
            },
            ...methods
        }

    });
}

function loadTree({data,methods,mounted,setting,urlList,urlEdit,dialogWidth,dialogHeight,urlDel,title}){
    return new Vue({
                el: '#app',
                data:{
                    ...{
                        setting:{...{
                            data: {
                                simpleData: {
                                    enable: true
                                },
                                key:{
                                    name:'text'
                                },
                                view: {
                                    showLine: true
                                }
                            }
                        },...setting}, ...data
                    }
                },

                mounted(){
                    this.loadData();
                    if(mounted) mounted();
                },
                methods:{...{
                    async loadData(){
                        let {data} = await axios.post(urlList, {
                            "cond": search2Cond(this.search, "AND")
                        });
                        if (data.code == 0) {
                            $.fn.zTree.init($("#"+this.treeName), this.setting, data.body);//.expandAll(true);
                        }
                        else {
                            //v.$message.error(data.message);
                            $.fn.zTree.init($("#"+this.treeName), this.setting, null);
                        }

                    },
                    reload(){
                        this.loadData();
                    },
                    handleAddRoot(){
                        v.dialogShow(0,{title:'添加'+title,url:urlEdit,width:dialogWidth,height:dialogHeight});
                    },
                    handleAdd(){
                        let node = this.selection();
                        if(node!=null){
                            v.dialogShow(0,{title:'添加'+title,url:urlEdit+"?idParent="+node.id+"&gradationCode="+node.attr.gradationCode,width:dialogWidth,height:dialogHeight});
                        }
                    },
                    handleEdit(){
                        let node = this.selection();
                        if(node!=null){
                            v.dialogShow(0,{title:'修改'+title,url:urlEdit+"?id="+node.id,width:dialogWidth,height:dialogHeight});
                        }
                    },
                    async handleDel(){
                        let node = this.selection(true,true);
                        if(node!=null){
                            let r = await v.$confirm('此操作将删除该' + title + ', 是否继续?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'}).catch(() => {});
                            if(r=='confirm') {
                                let {data} = await axios.delete(urlDel + node.id);
                                if (data.code == 0) {
                                    this.$message({type: 'success', message: '删除成功!'});
                                    this.tree().removeNode(node);
                                }
                                else {
                                    this.$message({type: 'error', message: data.message});

                                }
                            }
                        }
                    },
                    selection(leaf,isSelect){
                        let treeObj = this.tree();
                        let nodes = isSelect ? treeObj.getCheckedNodes() : treeObj.getSelectedNodes();
                        if(nodes==null || nodes.length==0){
                            v.$message.error("必须选中一项才能操作");
                        }
                        else if(leaf && nodes[0].children && nodes[0].children.length>0){
                            v.$message.error("必须选中叶节点才能操作");
                        }
                        else{
                            return nodes[0];
                        }
                    },
                    tree(){
                        return $.fn.zTree.getZTreeObj(this.treeName);
                    }
                },...methods}
    });
}

function loadForm({data,methods,formData,rules,urlSave,iframe}){
    return new Vue({
        el: '#app',
        data() {
            return {
                ...{
                    formData:formData,
                    rules: rules
                },...data
            }
        },
        methods: {
            ...{
                async add(){
                    let flag = await this.addOrSave();
                    if(flag) this.$refs['formData'].resetFields();
                },
                async addOrSave(){
                    let valid = await this.$refs['formData'].validate().catch(() => { return false;});
                    if(valid){
                        let {data} = await axios.post(urlSave, this.formData);
                        if (data.code == 0) {

                        }
                        else {
                            v.$message.error(data.message);
                            valid = false;
                        }
                    }
                    return valid;
                },
                async save(){
                    let flag = await this.addOrSave();
                    if(flag) this.close();
                },
                close(){
                    setTimeout(function () { v.dialogHide(0); }, 0);
                }
            },
            ...methods
        }

    });
}

function checkSelectDate(search) {
    if (search.gmtModified[0].data != '' && search.gmtModified[1].data != ''){
        var startTime=Date.parse(new Date(search.gmtModified[0].data));
        var endTime=Date.parse(new Date(search.gmtModified[1].data));
        if (((endTime-startTime)/1000/3600/24) < 0){
            v.$message.error('结束时间不能小于开始时间');
            search.gmtModified[1].data = '';
        }
    }
}