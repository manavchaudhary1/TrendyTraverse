
export interface ReviewDTO {
  reviewId: number;
  productId: number;
  stars: number;
  reviewDate: string;
  verifiedPurchase: boolean;
  manufacturerReplied: boolean;
  userId: string | null;
  title: string;
  reviewText: string;
  totalFoundHelpful: number;
  images: string[] | null;
}

export interface ReviewData {
  stars: number;
  title: string;
  reviewText: string;
  images?: string[];
}
