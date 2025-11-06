import React from "react";
import { Star, MessageCircle, Send, X, Edit3 } from "lucide-react";

const RatingItem = ({
  rating,
  replyingId,
  replyText,
  onStartReply,
  onCancelReply,
  onSubmitReply,
  onReplyTextChange,
}) => {
  const { id, username, rating: stars, product, comment, date, reply } = rating;
  const icon = username?.charAt(0)?.toUpperCase() || "?";

  return (
    <div className="border border-gray-200 rounded-xl p-4 hover:shadow-md transition-all bg-white">
      <div className="flex items-start gap-3">
        <div className="w-9 h-9 rounded-full bg-orange-500 text-white flex items-center justify-center font-semibold text-sm">
          {icon}
        </div>

        <div className="flex-1">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm font-medium text-gray-900">{username}</p>
              <p className="text-xs text-gray-500">{date}</p>
            </div>
            <div className="mt-1 sm:mt-0 flex items-center gap-1">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={i}
                  size={15}
                  className={
                    i < stars
                      ? "text-yellow-400 fill-yellow-400"
                      : "text-gray-300"
                  }
                />
              ))}
              <span className="ml-1 text-xs text-gray-600 font-medium">
                {stars}/5
              </span>
            </div>
          </div>

          <h3 className="mt-2 font-semibold text-gray-800 text-base">
            {product}
          </h3>

          <p className="text-gray-700 mt-2 text-sm leading-relaxed">
            {comment}
          </p>

          {reply ? (
            replyingId === id ? (
              <div className="mt-3 ml-4 border-l-4 border-blue-200 pl-3 bg-blue-50 rounded-md py-3">
                <textarea
                  className="w-full border border-gray-300 rounded-lg p-2 text-sm focus:ring-2 focus:ring-blue-400 focus:border-blue-400 outline-none"
                  rows={2}
                  value={replyText}
                  onChange={(e) => onReplyTextChange(e.target.value)}
                />
                <div className="flex gap-2 justify-end mt-2">
                  <button
                    onClick={onCancelReply}
                    className="px-3 py-1.5 text-sm flex items-center gap-1 text-gray-500 hover:text-gray-700"
                  >
                    <X size={14} /> Hủy
                  </button>
                  <button
                    onClick={() => onSubmitReply(id)}
                    className="px-3 py-1.5 bg-blue-600 text-white rounded-md text-sm flex items-center gap-1 hover:bg-blue-700"
                  >
                    <Send size={14} /> Lưu
                  </button>
                </div>
              </div>
            ) : (
              <div className="mt-3 ml-4 border-l-4 border-blue-200 pl-3 bg-blue-50/60 rounded-md py-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-1 mb-1">
                    <MessageCircle size={14} className="text-blue-500" />
                    <span className="text-sm font-semibold text-blue-700">
                      Phản hồi của cửa hàng
                    </span>
                  </div>
                  <button
                    onClick={() => onStartReply(id, reply)}
                    className="text-xs text-blue-500 hover:text-blue-700 flex items-center gap-1"
                  >
                    <Edit3 size={12} /> Sửa
                  </button>
                </div>
                <p className="text-sm text-gray-700">{reply}</p>
              </div>
            )
          ) : replyingId === id ? (
            <div className="mt-3 ml-4 border-l-4 border-gray-200 pl-3 bg-gray-50 rounded-md py-3">
              <textarea
                className="w-full border border-gray-300 rounded-lg p-2 text-sm focus:ring-2 focus:ring-blue-400 focus:border-blue-400 outline-none"
                placeholder="Nhập phản hồi của bạn..."
                rows={2}
                value={replyText}
                onChange={(e) => onReplyTextChange(e.target.value)}
              />
              <div className="flex gap-2 justify-end mt-2">
                <button
                  onClick={onCancelReply}
                  className="px-3 py-1.5 text-sm flex items-center gap-1 text-gray-500 hover:text-gray-700"
                >
                  <X size={14} /> Hủy
                </button>
                <button
                  onClick={() => onSubmitReply(id)}
                  className="px-3 py-1.5 bg-blue-600 text-white rounded-md text-sm flex items-center gap-1 hover:bg-blue-700"
                >
                  <Send size={14} /> Gửi
                </button>
              </div>
            </div>
          ) : (
            <button
              onClick={() => onStartReply(id)}
              className="mt-3 ml-4 text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1"
            >
              <MessageCircle size={14} /> Phản hồi
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default RatingItem;
