package com.ecit.lucene;

import com.ecit.common.constants.Constants;
import com.ecit.lucene.ik.IKAnalyzer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 *  LuenceProcess.java  
 *  @version ： 1.1
 *  @since   ： 1.0      创建时间:    Apr 3, 2013        11:48:11 AM
 *  TODO     : Luence中使用IK分词器
 *
 */
public class LuceneUtil {

    private static final Logger LOGGER = LogManager.getLogger(LuceneUtil.class);
    
    private Directory directory ;
    private Analyzer analyzer ;
    
    /**
     * 带参数构造,参数用来指定索引文件目录
     * @param indexFilePath
     */
    public LuceneUtil(String indexFilePath){
        try {
            directory = FSDirectory.open(Paths.get(indexFilePath));
            analyzer = new IKAnalyzer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 默认构造,使用系统默认的路径作为索引
     */
    public LuceneUtil(){
        this(Constants.LUCENE_INDICES);
    }

    /**
     * 创建索引
     * Description：
     * @author shwang Apr 3, 2013
     * @throws Exception
     */
    public void createIndex(List<JsonObject> list, boolean isDelete){
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
            /*if(isDelete){
                indexWriter.deleteAll();
                LOGGER.info("lucene delete all index");
            }*/
            for(int i=0; i<list.size(); i++){
                JsonObject json = list.get(i);
                indexWriter.addDocument(addDocument(json));
                //LOGGER.info("lucene create index: {}", json::encodePrettily);
            }

            indexWriter.close();
        } catch (IOException e) {
            LOGGER.info("lucene create index fail: ", e);
        }
    }
    
    /**
     * 
     * Description：
     * @author dennisit@163.com Apr 3, 2013
     * @return
     */
    public Document addDocument(JsonObject obj){
        Document doc = new Document();
        //Field.Index.NO 表示不索引
        //Field.Index.ANALYZED 表示分词且索引
        //Field.Index.NOT_ANALYZED 表示不分词且索引
        FieldType storeType = new FieldType();
        storeType.setStored(true);
        storeType.setTokenized(false);

        FieldType tokenType = new FieldType();
        tokenType.setStored(true);
        tokenType.setTokenized(true);

        Iterator<Map.Entry<String, Object>> it = obj.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getValue() instanceof String) {
                doc.add(new TextField(entry.getKey(), entry.getValue().toString(), Field.Store.YES));
                //doc.add(new Field(entry.getKey(), entry.getValue().toString(), tokenType));
            } else {
                doc.add(new Field(entry.getKey(), entry.getValue().toString(), storeType));
            }
        }
        return doc;
    }
    
    /**
     * 
     * Description： 更新索引
     * @author dennisit@163.com Apr 3, 2013
     * @param id
     * @param updateJson
     */
    public void update(Integer id, JsonObject updateJson){
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
            Document document = addDocument(updateJson);
            Term term = new Term("id",String.valueOf(id));
            indexWriter.updateDocument(term, document);
            indexWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * Description：按照ID进行索引
     * @author shwang
     * @param id
     */
    public void delete(Integer id){
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
            Term term = new Term("id",String.valueOf(id));
            indexWriter.deleteDocuments(term);
            indexWriter.close();
        } catch (Exception e) {
            LOGGER.error("delete lucene index error: ", e);
        }
    }

    /**
     * 删除所有索引
     */
    public void deleteAll(){
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
            indexWriter.deleteAll();
            indexWriter.close();
        } catch (Exception e) {
            LOGGER.error("delete all lucene index error: ", e);
        }
    }
    
    /**
     * Description：查询
     * @author shwang
     */
    public JsonObject search(String[] fields, String keyword, int size){

        IndexReader indexReader = null;
        IndexSearcher indexSearcher = null;
        JsonObject result = new JsonObject();
        try {
            // 根据目录打开一个indexReader
            indexReader = DirectoryReader.open(directory);
            indexSearcher = new IndexSearcher(indexReader);

            MultiFieldQueryParser queryParser =new MultiFieldQueryParser(fields, analyzer);
            Query query = queryParser.parse(keyword);
            
            //返回前number条记录
            TopDocs topDocs = indexSearcher.search(query, size);
            result.put("total", topDocs.totalHits.value);

            //高亮显示
            /*创建高亮器,使搜索的结果高亮显示
                SimpleHTMLFormatter：用来控制你要加亮的关键字的高亮方式
                此类有2个构造方法
                1：SimpleHTMLFormatter()默认的构造方法.加亮方式：<B>关键字</B>
                2：SimpleHTMLFormatter(String preTag, String postTag).加亮方式：preTag关键字postTag
             */
            Formatter formatter = new SimpleHTMLFormatter("<font color='red'>","</font>");    
            /*QueryScorer 是内置的计分器。计分器的工作首先是将片段排序。QueryScorer使用的项是从用户输入的查询中得到的；
                它会从原始输入的单词、词组和布尔查询中提取项，并且基于相应的加权因子（boost factor）给它们加权。
                为了便于QueryScoere使用，还必须对查询的原始形式进行重写。
                比如，带通配符查询、模糊查询、前缀查询以及范围查询 等，都被重写为BoolenaQuery中所使用的项。
                在将Query实例传递到QueryScorer之前，可以调用Query.rewrite (IndexReader)方法来重写Query对象 
             */
            Scorer fragmentScorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter,fragmentScorer);
            Fragmenter fragmenter = new SimpleFragmenter(100);
            /* Highlighter利用Fragmenter将原始文本分割成多个片段。
               内置的SimpleFragmenter将原始文本分割成相同大小的片段，片段默认的大小为100个字符。这个大小是可控制的。
             */
            highlighter.setTextFragmenter(fragmenter);
            
            JsonArray hits = new JsonArray();
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for(ScoreDoc scDoc : scoreDocs) {
                JsonObject docJson = new JsonObject().put("score", scDoc.score);
                Document document = indexSearcher.doc(scDoc.doc);
                Iterator<IndexableField> iterator = document.iterator();
                while (iterator.hasNext()) {
                    IndexableField indexableField = iterator.next();
                    docJson.put(indexableField.name(), document.get(indexableField.name()));
                    /*if (Arrays.asList(fields).contains(indexableField.name())) {
                        docJson.put(indexableField.name(), highlighter.getBestFragment(analyzer, indexableField.name(), document.get(indexableField.name())));
                    } else {
                        docJson.put(indexableField.name(), document.get(indexableField.name()));
                    }*/
                }
                hits.add(docJson);
            }
            result.put("hits", hits);
        } catch (Exception e) {
            LOGGER.error("lucene search error: ", e);
        }finally{
            try {
                indexReader.close();
            } catch (IOException e) {
                LOGGER.error("lucene indexReader close error: ", e);
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        LuceneUtil luceneProcess = new LuceneUtil(Constants.LUCENE_INDICES + "food");
        /*try {
            luceneProcess.createIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //修改测试
        //luceneProcess.update(2, "测试内容", "修改测试。。。");
        
        //查询测试
        String [] fields = {"name"};
        JsonObject list = luceneProcess.search(fields,"炒饭", 10);
        System.out.println(list);
        //删除测试
        //luenceProcess.delete(1);
        
    }
}