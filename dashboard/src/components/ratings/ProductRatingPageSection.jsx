import React, { useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { MessageCircle, Filter, Star } from "lucide-react";
import RatingItem from "./RatingItem";

// fake data
const fakeRatings = [
  {
    id: 1,
    productId: 101,
    product: "Tai nghe Sony WH-1000XM5",
    username: "Nguyễn Văn A",
    rating: 5,
    comment: "Chất lượng âm thanh tuyệt vời, pin lâu.",
    date: "2025-10-02",
    reply: "Cảm ơn bạn A! Mong bạn tiếp tục ủng hộ sản phẩm của Sony.",
    productImage: "https://cdn.tgdd.vn/Products/Images/54/284946/sony-wh1000xm5-den-thumb-600x600.jpg",
  },
  {
    id: 2,
    productId: 102,
    product: "iPhone 15 Pro",
    username: "Trần Thị B",
    rating: 4,
    comment: "Máy chạy mượt nhưng hơi nóng.",
    date: "2025-09-27",
    reply: null,
    productImage: "https://cdn.tgdd.vn/Products/Images/42/306996/iphone-15-pro-max-titan-thumb-600x600.jpg",
  },
  {
    id: 3,
    productId: 101,
    product: "Tai nghe Sony WH-1000XM5",
    username: "Phạm Minh C",
    rating: 5,
    comment: "Thiết kế mỏng nhẹ, pin cực trâu.",
    date: "2025-09-18",
    reply: "Cảm ơn anh Minh C đã tin tưởng sản phẩm của Sony!",
    productImage: "https://cdn.tgdd.vn/Products/Images/54/284946/sony-wh1000xm5-den-thumb-600x600.jpg",
  },
];

const ProductRatingListSection = () => {
  const { productId } = useParams(); // 👉 lấy productId từ URL nếu có

  const [ratings, setRatings] = useState(fakeRatings);
  const [replyingId, setReplyingId] = useState(null);
  const [replyText, setReplyText] = useState("");
  const [filterStar, setFilterStar] = useState("ALL");
  const [filterReply, setFilterReply] = useState("ALL");
  const [sortBy, setSortBy] = useState("newest");
  const [filterProduct, setFilterProduct] = useState("ALL");

  // Nếu có productId trong URL => ưu tiên lọc theo đó
  const filteredRatings = useMemo(() => {
    let result = [...ratings];

    // 🔹 Tự động lọc theo productId nếu có trên URL
    if (productId) result = result.filter((r) => r.productId === Number(productId));
    else if (filterProduct !== "ALL")
      result = result.filter((r) => r.productId === Number(filterProduct));

    if (filterStar !== "ALL") result = result.filter((r) => r.rating === Number(filterStar));
    if (filterReply === "REPLIED") result = result.filter((r) => r.reply);
    else if (filterReply === "UNREPLIED") result = result.filter((r) => !r.reply);

    switch (sortBy) {
      case "oldest":
        result.sort((a, b) => new Date(a.date) - new Date(b.date));
        break;
      case "highest":
        result.sort((a, b) => b.rating - a.rating);
        break;
      case "lowest":
        result.sort((a, b) => a.rating - b.rating);
        break;
      default:
        result.sort((a, b) => new Date(b.date) - new Date(a.date));
    }
    return result;
  }, [ratings, filterStar, filterReply, sortBy, filterProduct, productId]);

  const handleStartReply = (id, currentReply = "") => {
    setReplyingId(id);
    setReplyText(currentReply);
  };
  const handleCancelReply = () => {
    setReplyingId(null);
    setReplyText("");
  };
  const handleSubmitReply = (id) => {
    if (!replyText.trim()) return;
    setRatings((prev) =>
      prev.map((r) => (r.id === id ? { ...r, reply: replyText.trim() } : r))
    );
    handleCancelReply();
  };

  // Lấy thông tin sản phẩm (khi ở /products/:id/ratings)
  const productInfo = productId
    ? ratings.find((r) => r.productId === Number(productId))
    : null;

  return (
    <div className="bg-white shadow-sm rounded-2xl p-6 border border-gray-100">
      {/* Nếu đang ở trang riêng sản phẩm */}
      {productInfo && (
        <div className="flex items-center gap-4 mb-6 border-b pb-4">
          <img
            src={productInfo.productImage}
            alt={productInfo.product}
            className="w-20 h-20 object-cover rounded-lg border"
          />
          <div>
            <h3 className="text-lg font-semibold text-gray-900">{productInfo.product}</h3>
            <div className="flex items-center gap-1 mt-1">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={i}
                  size={16}
                  className={
                    i < Math.round(
                      ratings
                        .filter((r) => r.productId === Number(productId))
                        .reduce((acc, r) => acc + r.rating, 0) /
                        ratings.filter((r) => r.productId === Number(productId)).length
                    )
                      ? "text-yellow-400 fill-yellow-400"
                      : "text-gray-300"
                  }
                />
              ))}
              <span className="ml-2 text-sm text-gray-600">
                {ratings.filter((r) => r.productId === Number(productId)).length} đánh giá
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Header lọc */}
      {!productId && (
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-5">
          <h2 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
            <MessageCircle size={18} /> Danh sách đánh giá
          </h2>

          <div className="flex flex-wrap items-center gap-3 text-sm">
            <div className="flex items-center gap-1">
              <Filter size={14} className="text-gray-500" />
              <span className="font-medium text-gray-600">Lọc:</span>
            </div>

            {/* Lọc theo sản phẩm */}
            <select
              value={filterProduct}
              onChange={(e) => setFilterProduct(e.target.value)}
              className="border border-gray-300 rounded-md px-2 py-1 focus:ring-1 focus:ring-blue-400"
            >
              <option value="ALL">Tất cả sản phẩm</option>
              {[...new Set(ratings.map((r) => r.productId))].map((id) => {
                const p = ratings.find((r) => r.productId === id);
                return (
                  <option key={id} value={id}>
                    {p.product}
                  </option>
                );
              })}
            </select>

            {/* Các bộ lọc khác */}
            <select
              value={filterStar}
              onChange={(e) => setFilterStar(e.target.value)}
              className="border border-gray-300 rounded-md px-2 py-1 focus:ring-1 focus:ring-blue-400"
            >
              <option value="ALL">Tất cả sao</option>
              {[5, 4, 3, 2, 1].map((s) => (
                <option key={s} value={s}>
                  {s} sao
                </option>
              ))}
            </select>

            <select
              value={filterReply}
              onChange={(e) => setFilterReply(e.target.value)}
              className="border border-gray-300 rounded-md px-2 py-1 focus:ring-1 focus:ring-blue-400"
            >
              <option value="ALL">Tất cả phản hồi</option>
              <option value="REPLIED">Đã phản hồi</option>
              <option value="UNREPLIED">Chưa phản hồi</option>
            </select>

            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="border border-gray-300 rounded-md px-2 py-1 focus:ring-1 focus:ring-blue-400"
            >
              <option value="newest">Mới nhất</option>
              <option value="oldest">Cũ nhất</option>
              <option value="highest">Điểm cao nhất</option>
              <option value="lowest">Điểm thấp nhất</option>
            </select>
          </div>
        </div>
      )}

      {/* Danh sách đánh giá */}
      <div className="space-y-5">
        {filteredRatings.length === 0 ? (
          <p className="text-gray-500 text-sm italic text-center py-6">
            Không có đánh giá nào phù hợp.
          </p>
        ) : (
          filteredRatings.map((r) => (
            <RatingItem
              key={r.id}
              rating={r}
              replyingId={replyingId}
              replyText={replyText}
              onStartReply={handleStartReply}
              onCancelReply={handleCancelReply}
              onSubmitReply={handleSubmitReply}
              onReplyTextChange={setReplyText}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default ProductRatingListSection;
