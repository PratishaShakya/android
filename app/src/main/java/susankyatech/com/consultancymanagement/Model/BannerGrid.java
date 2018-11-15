package susankyatech.com.consultancymanagement.Model;

import java.util.List;

import susankyatech.com.consultancymanagement.Interfaces.HomeItems;

public class BannerGrid implements HomeItems {
    public List<BannerItem> bannerItemList;

    public BannerGrid(List<BannerItem> bannerItemList) {
        this.bannerItemList = bannerItemList;
    }
}
