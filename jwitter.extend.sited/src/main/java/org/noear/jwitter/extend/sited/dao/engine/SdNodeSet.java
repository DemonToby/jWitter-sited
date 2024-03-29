package org.noear.jwitter.extend.sited.dao.engine;


import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuety on 15/8/21.
 */
public class SdNodeSet implements ISdNode{

    List<ISdNode> _items = new ArrayList<>();

    protected void release(){
        _items.clear();
    }

    public final SdSource source;

    //---------------

    public SdNodeSet(SdSource s){
        source = s;
    }

    public void OnDidInit(){

    }

    private int _dtype=0;
    public  int dtype() {
        if (_dtype > 0)
            return _dtype;
        else
            return 1;
    }//数据类型
    private int _btype=0;
    public int btype(){
        if(_btype>0)
            return _btype;
        else
            return dtype();
    }

    public int nodeType(){return 2;}
    public String nodeName(){return name;}
    @Override
    public boolean  isEmpty(){
        return _items.size()==0;
    }

    public String name;
    public SdAttributeList attrs = new SdAttributeList();


    protected SdNodeSet buildForNode(Element element) {
        if(element==null)
            return this;

        name = element.getTagName();

        _items.clear();
        attrs.clear();

        {
            NamedNodeMap temp = element.getAttributes();
            for (int i = 0, len = temp.getLength(); i < len; i++) {
                Node p = temp.item(i);
                attrs.set(p.getNodeName(), p.getNodeValue());
            }
        }

        {
            NodeList temp = element.getChildNodes();
            for (int i = 0, len = temp.getLength(); i < len; i++) {
                Node p = temp.item(i);

                if (p.getNodeType() == Node.ELEMENT_NODE && p.hasAttributes() == false && p.hasChildNodes()) {
                    if(p.getChildNodes().getLength()==1) {
                        Node p2 = p.getFirstChild();
                        if (p2.getNodeType() == Node.TEXT_NODE) {
                            attrs.set(p.getNodeName(), p2.getNodeValue());
                        }
                    }
                }
            }
        }

        _dtype  = attrs.getInt("dtype");
        _btype  = attrs.getInt("btype");


        NodeList xList = element.getChildNodes();

        for (int i = 0, len = xList.getLength(); i < len; i++) {
            Node n1 = xList.item(i);
            if (n1.getNodeType() == Node.ELEMENT_NODE) {
                Element e1 = (Element) n1;
                String tagName = e1.getTagName();

                if (e1.hasAttributes()) {//说明是Node类型
                    SdNode temp = SdApi.createNode(source,tagName).buildForNode(e1);
                    this.add(temp);
                } else if(e1.hasChildNodes() && e1.getChildNodes().getLength()>1) {//说明是Set类型
                    SdNodeSet temp = SdApi.createNodeSet(source, tagName).buildForNode(e1);
                    this.add(temp);
                }
            }
        }

        OnDidInit();
        return this;
    }

    public Iterable<ISdNode> nodes(){
        return _items;
    }

    public ISdNode get(String name){
        for(ISdNode n : _items){
            if(name.equals(n.nodeName()))
                return n;
        }

        return SdApi.createNode(source, name).buildForNode(null);
    }

    public SdNode nodeMatch(String url) {
        for (ISdNode n : _items) {
            SdNode n1 = (SdNode) n;
            if (n1.isMatch(url)) {
                return n1;
            }
        }

        return SdApi.createNode(source, null).buildForNode(null);
    }

    protected void add(ISdNode node){
        _items.add(node);
    }
}
