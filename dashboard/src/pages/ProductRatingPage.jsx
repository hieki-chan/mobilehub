import React from "react";
import { motion } from "framer-motion";
import { Star, ThumbsUp, MessageSquare, AlertCircle } from "lucide-react";

import Header from "../components/common_components/Header";
import StatCards from "../components/common_components/StatCards";
import ProductRatingListSection from "../components/ratings/ProductRatingPageSection";

const Rating_Stat = {
  totalReviews: "1,245",
  averageRating: "4.3",
  positiveFeedback: "86%",
  reportedReviews: "12",
};

const ProductRatingPage = () => {
  return (
    <div className="flex-1 overflow-auto relative z-10 bg-gray-50 text-gray-900">
      <Header
        path={[
          { label: "Mobilehub", to: "/" },
          { label: "Sản phẩm", to: "/products" },
          { label: "Đánh giá", to: "" }, 
        ]}
        userName="Admin"
      />

      <main className="py-6 px-4 lg:px-4">
        {/* Thống kê tổng quan */}
        <motion.div
          className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-7"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          <StatCards
            name="Tổng số đánh giá"
            icon={MessageSquare}
            value={Rating_Stat.totalReviews}
            color="#6366f1"
          />
          <StatCards
            name="Điểm trung bình"
            icon={Star}
            value={Rating_Stat.averageRating}
            color="#f59e0b"
          />
          <StatCards
            name="Tích cực (%)"
            icon={ThumbsUp}
            value={Rating_Stat.positiveFeedback}
            color="#10b981"
          />
          <StatCards
            name="Đánh giá bị báo cáo"
            icon={AlertCircle}
            value={Rating_Stat.reportedReviews}
            color="#ef4444"
          />
        </motion.div>


        <ProductRatingListSection />
      </main>
    </div>
  );
};

export default ProductRatingPage;
