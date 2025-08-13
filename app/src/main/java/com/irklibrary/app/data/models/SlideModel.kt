package com.irklibrary.app.data.models

data class SlideModel(
    val judul: String,
    val link: String
)

data class MataKuliahModel(
    val matkul: String,
    val slides: List<SlideModel>
)

data class SlideWithMatkulModel(
    val judul: String,
    val link: String,
    val matkul: String,
    val matkulCode: String
) {
    companion object {
        fun fromMataKuliahModel(mataKuliahModel: MataKuliahModel): List<SlideWithMatkulModel> {
            return mataKuliahModel.slides.map { slide ->
                SlideWithMatkulModel(
                    judul = slide.judul,
                    link = slide.link,
                    matkul = mataKuliahModel.matkul,
                    matkulCode = extractMatkulCode(mataKuliahModel.matkul)
                )
            }
        }

        private fun extractMatkulCode(matkul: String): String {
            return matkul.split(" ")[0]
        }
    }
}