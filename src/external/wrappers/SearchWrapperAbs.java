package external.wrappers;

import metasearch.Searcher;
import metasearch.cache.CacheContentManager;

import java.net.Proxy;
import java.util.List;

public abstract class SearchWrapperAbs implements SearchWrapper {

    public abstract List downloadResultContent(String query, Proxy proxy);

    public List getResultContent(String query, Proxy proxy, Searcher searcher){
        List contents = CacheContentManager.getInstance().loadContentFromCache(searcher,query);
        if(contents!=null){
            return contents;
        }else{
            contents = downloadResultContent(query, proxy);
            CacheContentManager.getInstance().saveContentInCache(contents,searcher,query);
            return contents;
        }
    };
}
