package com.example.physicshub.ui.language

data class TranslationStrings(
    val hello: String = "",
    val noticeBoard: String = "",
    val viewMore: String = "",
    val upcomingEvent: String = "",
    val examArchive: String = "",
    val notifications: String = "",
    val all: String = "",
    val unread: String = "",
    val noUnreadNotices: String = "",
    val noNotices: String = "",
    val back: String = "",
    val placeholder: String = "",
    val academicAffairs: String = "",
    val research: String = "",
    val events: String = "",
    val general: String = "",
    val navHome: String = "",
    val navEvents: String = "",
    val navNotices: String = "",
    val navBooking: String = "",
    val navExams: String = ""
)

data class TranslationResponse(
    val en: TranslationStrings = TranslationStrings(),
    val vn: TranslationStrings = TranslationStrings()
)

val defaultEnglishStrings = TranslationStrings(
    hello = "Hello,",
    noticeBoard = "NOTICE BOARD",
    viewMore = "View more",
    upcomingEvent = "UPCOMING EVENT",
    examArchive = "EXAM ARCHIVE",
    notifications = "Notifications",
    all = "All",
    unread = "Unread",
    noUnreadNotices = "No unread notices",
    noNotices = "No notices",
    back = "Back",
    placeholder = "Placeholder",
    academicAffairs = "Academic Affairs",
    research = "Research",
    events = "Events",
    general = "General",
    navHome = "Home",
    navEvents = "Events",
    navNotices = "Notices",
    navBooking = "Booking",
    navExams = "Exams"
)

val defaultVietnameseStrings = TranslationStrings(
    hello = "Xin chào,",
    noticeBoard = "THÔNG BÁO",
    viewMore = "Xem thêm",
    upcomingEvent = "SỰ KIỆN SẮP TỚI",
    examArchive = "XEM TÀI LIỆU",
    notifications = "Thông báo",
    all = "Tất cả",
    unread = "Chưa đọc",
    noUnreadNotices = "Không có thông báo chưa đọc",
    noNotices = "Không có thông báo",
    back = "Quay lại",
    placeholder = "Đang cập nhật",
    academicAffairs = "Học vụ",
    research = "Nghiên cứu",
    events = "Sự kiện",
    general = "Chung",
    navHome = "Trang chủ",
    navEvents = "Sự kiện",
    navNotices = "Thông báo",
    navExams = "Tài liệu"
)
