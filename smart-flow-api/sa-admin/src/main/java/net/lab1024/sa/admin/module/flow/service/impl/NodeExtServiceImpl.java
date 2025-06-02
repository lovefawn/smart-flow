package net.lab1024.sa.admin.module.flow.service.impl;

import org.dromara.warm.flow.ui.service.NodeExtService;
import org.dromara.warm.flow.ui.vo.NodeExt;
import org.dromara.warm.flow.ui.vo.NodeExt.ChildNode;
import org.dromara.warm.flow.ui.vo.NodeExt.DictItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程设计器-节点扩展属性
 *
 * @author warm
 */
@Component
public class NodeExtServiceImpl implements NodeExtService {

    @Override
    public List<NodeExt> getNodeExt() {
        List<NodeExt> nodeExts = new ArrayList<>();

//        // 第一个 NodeExt 对象
//        NodeExt nodeExt1 = new NodeExt();
//        nodeExt1.setCode("base");
//        nodeExt1.setDesc("基础设置扩展属性");
//        nodeExt1.setType(1);
//
//        List<ChildNode> childs1 = new ArrayList<>();
//
//        // 第一个 ChildNode 对象
//        ChildNode childNode1 = new ChildNode();
//        childNode1.setCode("base1");
//        childNode1.setLabel("输入框");
//        childNode1.setDesc("基础设置扩展属性1");
//        childNode1.setType(1);
//        childNode1.setMust(false);
//        childs1.add(childNode1);
//
//        // 第二个 ChildNode 对象
//        ChildNode childNode2 = new ChildNode();
//        childNode2.setCode("base2");
//        childNode2.setLabel("文本域");
//        childNode2.setDesc("基础设置扩展属性2");
//        childNode2.setType(2);
//        childNode2.setMust(false);
//        childs1.add(childNode2);
//
//        // 第三个 ChildNode 对象
//        ChildNode childNode3 = new ChildNode();
//        childNode3.setCode("base3");
//        childNode3.setLabel("下一步角色");
//        childNode3.setDesc("基础设置扩展属性3");
//        childNode3.setType(3);
//        childNode3.setMust(false);
//
//        List<DictItem> dictItems3 = new ArrayList<>();
//        dictItems3.add(new DictItem("普通角色", "common", true));
//        dictItems3.add(new DictItem("领导", "leader"));
//        dictItems3.add(new DictItem("员工", "yuangong"));
//        childNode3.setDict(dictItems3);
//        childs1.add(childNode3);

//        // 第四个 ChildNode 对象
//        ChildNode childNode4 = new ChildNode();
//        childNode4.setCode("base4");
//        childNode4.setLabel("单选框");
//        childNode4.setDesc("基础设置扩展属性4");
//        childNode4.setType(4);
//        childNode4.setMust(false);
//
//        List<DictItem> dictItems4 = new ArrayList<>();
//        dictItems4.add(new DictItem("是否弹窗选人", "1", true));
//        dictItems4.add(new DictItem("是否能委托", "2", true));
//        dictItems4.add(new DictItem("是否能转办", "3"));
//        dictItems4.add(new DictItem("是否能抄送", "4"));
//        dictItems4.add(new DictItem("是否显示退回", "5"));
//        dictItems4.add(new DictItem("是否能加签", "6"));
//        dictItems4.add(new DictItem("是否能减签", "7"));
//        childNode4.setDict(dictItems4);
//        childs1.add(childNode4);
//
//        nodeExt1.setChilds(childs1);
//        nodeExts.add(nodeExt1);

        // 第二个 NodeExt 对象
        NodeExt nodeExt2 = new NodeExt();
        nodeExt2.setCode("new_tabs");
        nodeExt2.setName("按钮权限");
        nodeExt2.setDesc("按钮权限设置");
        nodeExt2.setType(2);

        List<ChildNode> childs2 = new ArrayList<>();

        // 第一个 ChildNode 对象
        ChildNode childNode5 = new ChildNode();
        childNode5.setCode("new_tabs1");
        childNode5.setLabel("复选框");
        childNode5.setDesc("按钮权限1");
        childNode5.setType(4);
        childNode5.setMust(false);
        childNode5.setMultiple(true);

        List<DictItem> dictItems5 = new ArrayList<>();
        dictItems5.add(new DictItem("是否弹窗选人", "1"));
        dictItems5.add(new DictItem("是否能委托", "2"));
        dictItems5.add(new DictItem("是否能转办", "3", true));
        dictItems5.add(new DictItem("是否能抄送", "4", true));
        dictItems5.add(new DictItem("是否显示退回", "5"));
        dictItems5.add(new DictItem("是否能加签", "6"));
        dictItems5.add(new DictItem("是否能减签", "7"));
        childNode5.setDict(dictItems5);
        childs2.add(childNode5);

        ChildNode childNode6 = new ChildNode();
        childNode6.setCode("new_tabs2");
        childNode6.setLabel("下拉选-多选");
        childNode6.setDesc("基础设置扩展属性3");
        childNode6.setType(3);
        childNode6.setMust(false);
        childNode6.setMultiple(true);

        List<DictItem> dictItems6 = new ArrayList<>();
        dictItems6.add(new DictItem("选项A", "1", true));
        dictItems6.add(new DictItem("选项B", "2"));
        dictItems6.add(new DictItem("选项C", "3"));
        childNode6.setDict(dictItems6);
        childs2.add(childNode6);

        nodeExt2.setChilds(childs2);
        nodeExts.add(nodeExt2);

        return nodeExts;
    }
}
