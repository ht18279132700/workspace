package cn.com.pcauto.wenda.util.pager;

import org.apache.commons.lang.StringUtils;
import org.gelivable.web.Env;
import org.gelivable.web.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * 分页标签
 */
public class PagerTag extends TagSupport {

    private static final long serialVersionUID = -2915195316684396160L;
    private static final Logger LOG = LoggerFactory.getLogger(PagerTag.class);
    private int pageNo;		//页码
    private int pageSize;	//每页大小
    private int totalSize;	//总大小
    private int showNum = 10;	//显示多少页,默认10页
    private boolean showInput;	//是否显示“跳转”
    private String rewriteUrl; //静态URL
    private String SPAN_TAG = "&nbsp;<span>{0}</span>&nbsp;";
    private String A_TAG = "&nbsp;<a href=\"{0}\">{1}</a>&nbsp;";
    private String INPUT_TAG = "&nbsp;<i class=\"iNum\"><input type=\"text\" class=\"txt\""
            + " name=\"pageNo\" value=\"{0}\""
            + " onkeyup=\"pagerGo(this,event);\" title=\"输入页码，按回车跳转\"/> /{1}页</i>&nbsp;";

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public void setShowInput(boolean showInput) {
        this.showInput = showInput;
    }

    private int getTotalPage() {
        return totalSize / pageSize + (totalSize % pageSize == 0 ? 0 : 1);
    }

    public void setRewriteUrl(String rewriteUrl) {
        this.rewriteUrl = rewriteUrl;
    }

    @Override
    public int doStartTag() throws JspException {
        JspWriter jw = this.pageContext.getOut();
        try {
            if (totalSize <= 0) {
                return TagSupport.SKIP_BODY;
            }
            jw.write(getOutPutContent());
            jw.flush();
        } catch (IOException ex) {
            LOG.error("pager error", ex.getMessage());
        }
        return TagSupport.EVAL_BODY_INCLUDE;
    }

    private String getSpanTag(int pageNo, boolean dot) {
        if (dot) {
            return MessageFormat.format(SPAN_TAG, String.valueOf(pageNo + "..."));
        }
        return MessageFormat.format(SPAN_TAG, String.valueOf(pageNo));
    }

    private String getATag(String url, int pageNo, boolean dot, boolean front) {
        if (dot) {
            return MessageFormat.format(A_TAG, new Object[]{String.valueOf(getUrl(url, pageNo)),
                        String.valueOf(front ? "..." + pageNo : pageNo + "...")});
        }
        return MessageFormat.format(A_TAG, new Object[]{String.valueOf(getUrl(url, pageNo)),
                    String.valueOf(pageNo)});
    }

    private String getInputTag(int pageNo, int totalPage) {
        return MessageFormat.format(INPUT_TAG, new Object[]{String.valueOf(pageNo), String.valueOf(totalPage)});
    }

    private String getNextTag(String url, int pageNo) {
        return "<a class=\"next\" href=\"" + getUrl(url, pageNo + 1) + "\">下一页</a>";
    }

    private String getPrevTag(String url, int pageNo) {
        return "<a class=\"prev\" href=\"" + getUrl(url, pageNo - 1) + "\">上一页</a>";
    }

    private String getUrl(String url, int pageNo) {
        if (StringUtils.isNotBlank(rewriteUrl)) {
            return getRewriteUrl(url, pageNo);
        }
        return url + (url.indexOf('?') > 0 ? "&" : "?") + "pageNo=" + pageNo;
    }

    /**
     * 每月贡献排行榜静态url
     *
     * @param url
     * @param pageNo
     * @return
     */
    private String getRewriteUrl(String url, int pageNo) {
        if (pageNo == 1) {
            return rewriteUrl;
        }
        return rewriteUrl + pageNo;
    }

    /**
     * 分页循环数组计算
     *
     * @param curPage 当前页
     * @param showNum 页面显示的页数
     * @param totalPage 总页数
     * @return Integer[],循环的开始和结束
     */
    public Integer[] pagesSplit(int curPage, int showNum, int totalPage) {

        int prevNum = 0;  //前移偏移量
        int nextNum = 0;  //后移偏移量
        if (showNum % 2 == 0) {
            prevNum = showNum / 2;
            nextNum = showNum / 2;
        } else {
            prevNum = (showNum % 2 == 0) ? (showNum / 2) : (showNum / 2 + 1);
            nextNum = showNum / 2;
        }

        int startIndex = 0;
        int endIndex = 0;
        if (curPage - prevNum < 0) {
            nextNum = nextNum - (curPage - prevNum);
            startIndex = 1;
        } else {
            startIndex = curPage - prevNum;
        }
        if (curPage + nextNum > totalPage) {
            startIndex = startIndex - (curPage + nextNum - totalPage);
        }
        if (startIndex < 1) {
            startIndex = 1;
        }
        endIndex = curPage + nextNum;
        if (endIndex > totalPage) {
            endIndex = totalPage;
        }

        return new Integer[]{startIndex, endIndex};
    }

    /**
     * 获取输出页面的内容
     *
     * @return	输出分页HTML
     */
    public String getOutPutContent() {
        int totalPage = getTotalPage();
        if (totalPage <= 1) {
            return "";
        }
        Env env = EnvUtils.getEnv();
        String url = PagerUtil.getPagerUrl(env.getRequest());
        StringBuilder sb = new StringBuilder();

        Integer[] array = pagesSplit(pageNo, showNum, totalPage);
        int start = array[0];
        int end = array[1];

        if (pageNo > 1) {
            sb.append(getPrevTag(url, pageNo));   //显示“上一页”
        }
        if (start > 1) {
            if (start > 2) {
                sb.append(getATag(url, 1, true, false)); //显示“1...”
            } else {
                sb.append(getATag(url, 1, false, false));//显示“1”
            }
        }
        for (int i = start; i < end; i++) {       //循环数组
            if (i != pageNo) {
                sb.append(getATag(url, i, false, false));
            } else {
                sb.append(getSpanTag(i, false));
            }
        }
        if (end == pageNo) {
            sb.append(getSpanTag(totalPage, false));
        } else if (pageNo < totalPage) {
            if (totalPage - pageNo > showNum) {
                sb.append(getATag(url, totalPage, true, true));
            } else {
                sb.append(getATag(url, totalPage, false, false));
            }
        }
        if (showInput && (start != end)) {
            sb.append(getInputTag(pageNo, totalPage));   //显示“跳转”
        }
        if (pageNo < totalPage) {
            sb.append(" ");
            sb.append(getNextTag(url, pageNo));          //显示“下一页”
        }
        return sb.toString();
    }
}
