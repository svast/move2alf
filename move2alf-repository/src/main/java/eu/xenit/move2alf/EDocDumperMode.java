package eu.xenit.move2alf;

public enum EDocDumperMode {
  TStore, // default
  TStoreIfNotExists,
  TPreview,
  TListIfNotExists,
  TListIfNotExistsFilenameOnly,
  TUpdate,
  TUpdateIfSizeIsDifferent,
  TStoreOrUpdate,
  TStoreOrUpdateIfSizeIsDifferent,
  // because of a limitation in axis, the 2 methods above only work for files
  // upto 20MB, for big files one can use a variant which is slower (for an update
  // it will first delete and then store)
  TStoreOrUpdateBigFiles,
  TRemoveZeroSizedDocsBelowParentSpace,
  TDelete,
  TTiff2PdfOnly // needed for a single conversion, without storing in alfresco
}
